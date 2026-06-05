package com.chat.was.chat.service.impl;

import com.chat.was.auth.dao.AdminUserRepository;
import com.chat.was.chat.dao.ChatMapper;
import com.chat.was.chat.dao.ChatMessageRepository;
import com.chat.was.chat.dao.ChatRoomRepository;
import com.chat.was.chat.service.ChatService;
import com.chat.was.chat.vo.*;
import com.chat.was.chat.vo.ChatMessageVo;
import com.chat.was.global.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅 비즈니스 로직 구현체.
 * Google Gemini 2.5 Flash API 연동, 대화 맥락 유지, 채팅방 제목 자동 생성 기능을 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AdminUserRepository adminUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;
    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;

    /**
     * Gemini API 모델 경로 상수 (gemini-2.5-flash 사용)
     */
    private static final String GEMINI_MODEL = "/v1beta/models/gemini-2.5-flash:generateContent";

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomVo> getRooms(String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        return chatRoomRepository.findByUserSeqAndUseYnOrderByUpdatedAtDesc(userSeq, "Y")
                .stream()
                .map(r -> new ChatRoomVo(r.getRoomId(), r.getTitle(), r.getUpdatedAt(), r.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomVo> searchRooms(String adminId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getRooms(adminId);
        }
        Long userSeq = resolveUserSeq(adminId);
        return chatMapper.searchRooms(userSeq, keyword)
                .stream()
                .map(r -> new ChatRoomVo(r.getRoomId(), r.getTitle(), r.getUpdatedAt(), r.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Gemini API 호출 흐름:
     * 1. 신규 방이면 ChatRoom 생성
     * 2. 최근 5쌍(10개) 메시지를 DB에서 로드하여 대화 맥락 구성
     * 3. 사용자 메시지 저장 후 Gemini API 호출
     * 4. AI 응답 저장
     * 5. 첫 번째 메시지이면 별도 API 호출로 채팅방 제목 생성
     */
    @Override
    @Transactional
    public ChatSendResponseVo sendMessage(ChatSendRequestVo request) {
        Long userSeq = resolveUserSeq(request.getAdminId());

        // 신규 채팅방 생성 또는 기존 방 조회
        boolean isNewRoom = (request.getRoomId() == null);
        ChatRoom chatRoom;
        if (isNewRoom) {
            chatRoom = chatRoomRepository.save(ChatRoom.builder()
                    .userSeq(userSeq)
                    .build());
        } else {
            chatRoom = chatRoomRepository.findByRoomIdAndUserSeqAndUseYn(
                    request.getRoomId(), userSeq, "Y")
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        }
        Long roomId = chatRoom.getRoomId();

        // 첫 번째 메시지 여부 판단 (제목 자동 생성 트리거)
        long messageCount = chatMessageRepository.countByRoomId(roomId);
        boolean isFirstMessage = (messageCount == 0);

        // 대화 맥락용 기존 메시지 로드 (최신순 10개 → 시간순 정렬)
        List<ChatMessage> recentMessages = chatMessageRepository.findTop10ByRoomIdOrderByCreatedAtDesc(roomId);
        Collections.reverse(recentMessages); // 오래된 것부터 정렬

        // 사용자 메시지 저장
        chatMessageRepository.save(ChatMessage.builder()
                .roomId(roomId)
                .senderType("USER")
                .content(request.getContent())
                .build());

        // Gemini API 호출 - 대화 맥락 포함
        String aiResponse = callGemini(recentMessages, request.getContent());

        // AI 응답 저장
        chatMessageRepository.save(ChatMessage.builder()
                .roomId(roomId)
                .senderType("AI")
                .content(aiResponse)
                .build());

        // 첫 번째 메시지인 경우 채팅방 제목 자동 생성
        String newTitle = null;
        if (isFirstMessage) {
            newTitle = generateRoomTitle(request.getContent());
            chatRoom.updateTitle(newTitle);
            chatRoomRepository.save(chatRoom);
        }

        return new ChatSendResponseVo(roomId, aiResponse, isFirstMessage ? newTitle : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageVo> getMessages(Long roomId, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        // 소유자 검증: 해당 방이 요청한 사용자의 방인지 확인
        chatRoomRepository.findByRoomIdAndUserSeqAndUseYn(roomId, userSeq, "Y")
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(m -> new ChatMessageVo(m.getMessageId(), m.getRoomId(),
                        m.getSenderType(), m.getContent(), m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteRoom(Long roomId, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdAndUserSeqAndUseYn(roomId, userSeq, "Y")
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        // 메시지 물리 삭제 → 채팅방 논리 삭제 순서로 처리
        chatMessageRepository.deleteAllByRoomId(roomId);
        chatRoom.delete();
        chatRoomRepository.save(chatRoom);
        log.info("채팅방 삭제 완료 - roomId: {}, adminId: {}", roomId, adminId);
    }

    /**
     * adminId(로그인 ID)로 user_seq를 조회하는 내부 헬퍼.
     * 사용자가 존재하지 않으면 IllegalArgumentException을 던진다.
     *
     * @param adminId 조회할 관리자 로그인 아이디
     * @return admin_user.user_seq 값
     */
    private Long resolveUserSeq(String adminId) {
        return adminUserRepository.findByAdminId(adminId)
                .map(u -> u.getUserSeq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + adminId));
    }

    /**
     * Gemini 2.5 Flash API를 호출하여 AI 응답을 받는다.
     * 대화 맥락(recentMessages)을 contents 배열에 포함하여 전달한다.
     *
     * @param recentMessages 기존 대화 메시지 목록 (USER/AI 교대)
     * @param userInput      현재 사용자 입력
     * @return AI 응답 텍스트
     */
    private String callGemini(List<ChatMessage> recentMessages, String userInput) {
        try {
            List<Map<String, Object>> contents = buildContents(recentMessages, userInput);
            Map<String, Object> requestBody = Map.of("contents", contents);

            String url = GEMINI_MODEL + "?key=" + geminiConfig.getGeminiApiKey();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = geminiRestClient.post()
                    .uri(url)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (Exception e) {
            log.error("Gemini API 호출 오류: {}", e.getMessage());
            return "죄송합니다. AI 응답 생성 중 오류가 발생했습니다.";
        }
    }

    /**
     * Gemini API에 전달할 contents 배열 구성.
     * ChatMessage의 senderType을 Gemini의 role("user"/"model")로 변환한다.
     *
     * @param recentMessages 기존 메시지 목록
     * @param userInput      현재 사용자 입력
     * @return Gemini API 형식의 contents 목록
     */
    private List<Map<String, Object>> buildContents(List<ChatMessage> recentMessages, String userInput) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatMessage msg : recentMessages) {
            String role = "USER".equals(msg.getSenderType()) ? "user" : "model";
            contents.add(Map.of(
                    "role", role,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }
        // 현재 사용자 메시지 추가
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userInput))
        ));
        return contents;
    }

    /**
     * Gemini API 응답 Map에서 텍스트를 추출한다.
     *
     * @param response Gemini API 응답 Map
     * @return 추출된 텍스트 문자열
     */
    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    /**
     * 첫 번째 메시지를 기반으로 채팅방 제목을 생성한다.
     * Gemini API에 "10글자 내외 요약" 프롬프트를 별도로 요청한다.
     *
     * @param userInput 첫 번째 사용자 메시지
     * @return 생성된 채팅방 제목 (최대 20자로 truncate)
     */
    private String generateRoomTitle(String userInput) {
        try {
            String titlePrompt = "다음 메시지를 보고 대화 제목을 10글자 내외의 한국어로 간결하게 요약해줘. 제목만 답해줘, 따옴표 없이:\n" + userInput;
            List<Map<String, Object>> contents = List.of(
                    Map.of("role", "user", "parts", List.of(Map.of("text", titlePrompt)))
            );
            Map<String, Object> requestBody = Map.of("contents", contents);
            String url = GEMINI_MODEL + "?key=" + geminiConfig.getGeminiApiKey();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = geminiRestClient.post()
                    .uri(url)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String title = extractText(response).trim();
            // 20자 초과 시 truncate
            return title.length() > 20 ? title.substring(0, 20) : title;
        } catch (Exception e) {
            log.error("채팅방 제목 생성 오류: {}", e.getMessage());
            return userInput.length() > 20 ? userInput.substring(0, 20) : userInput;
        }
    }
}

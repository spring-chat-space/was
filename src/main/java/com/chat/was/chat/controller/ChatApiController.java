package com.chat.was.chat.controller;

import com.chat.was.chat.service.ChatService;
import com.chat.was.chat.vo.ChatMessageVo;
import com.chat.was.chat.vo.ChatRoomVo;
import com.chat.was.chat.vo.ChatSendRequestVo;
import com.chat.was.chat.vo.ChatSendResponseVo;
import com.chat.was.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 REST API 컨트롤러.
 * 채팅방 목록 조회, 검색, 메시지 전송, 삭제 요청을 처리한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    /**
     * 채팅방 목록 조회 API.
     *
     * @param adminId 조회할 사용자 아이디 (쿼리 파라미터)
     * @return 최근 수정 순 채팅방 목록
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomVo>>> getRooms(@RequestParam("adminId") String adminId) {
        log.info("채팅방 목록 조회 - adminId: {}", adminId);
        return ResponseEntity.ok(ApiResponse.success(chatService.getRooms(adminId)));
    }

    /**
     * 채팅방 검색 API.
     * 채팅방 제목 또는 메시지 내용에 키워드가 포함된 방을 반환한다.
     *
     * @param adminId 사용자 아이디
     * @param q       검색 키워드
     * @return 검색된 채팅방 목록
     */
    @GetMapping("/rooms/search")
    public ResponseEntity<ApiResponse<List<ChatRoomVo>>> searchRooms(
            @RequestParam("adminId") String adminId,
            @RequestParam(name = "q", required = false) String q) {
        log.info("채팅방 검색 - adminId: {}, keyword: {}", adminId, q);
        return ResponseEntity.ok(ApiResponse.success(chatService.searchRooms(adminId, q)));
    }

    /**
     * 메시지 전송 및 AI 응답 수신 API.
     * Gemini 1.5 Flash 모델 호출 후 응답을 반환한다.
     *
     * @param request 메시지 전송 요청 (adminId, roomId, content)
     * @return AI 응답, roomId, 신규 채팅방 제목(해당 시)
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatSendResponseVo>> sendMessage(@RequestBody ChatSendRequestVo request) {
        log.info("메시지 전송 - adminId: {}, roomId: {}", request.getAdminId(), request.getRoomId());
        return ResponseEntity.ok(ApiResponse.success(chatService.sendMessage(request)));
    }

    /**
     * 채팅방 메시지 히스토리 조회 API.
     *
     * @param roomId  조회할 채팅방 ID
     * @param adminId 소유자 확인용 사용자 아이디
     * @return 시간순 메시지 목록
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageVo>>> getMessages(
            @PathVariable("roomId") Long roomId,
            @RequestParam("adminId") String adminId) {
        log.info("채팅 메시지 조회 - roomId: {}, adminId: {}", roomId, adminId);
        return ResponseEntity.ok(ApiResponse.success(chatService.getMessages(roomId, adminId)));
    }

    /**
     * 채팅방 삭제 API (논리적 삭제).
     * use_yn을 'N'으로 변경한다.
     *
     * @param roomId  삭제할 채팅방 ID
     * @param adminId 소유자 확인용 사용자 아이디
     * @return 성공 시 200 OK
     */
    @PostMapping("/rooms/{roomId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable("roomId") Long roomId,
            @RequestParam("adminId") String adminId) {
        log.info("채팅방 삭제 - roomId: {}, adminId: {}", roomId, adminId);
        chatService.deleteRoom(roomId, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

package com.chat.was.chat.service;

import com.chat.was.chat.vo.ChatMessageVo;
import com.chat.was.chat.vo.ChatRoomVo;
import com.chat.was.chat.vo.ChatSendRequestVo;
import com.chat.was.chat.vo.ChatSendResponseVo;

import java.util.List;

/**
 * 채팅 비즈니스 로직 인터페이스.
 * 채팅방 CRUD, 메시지 전송(Gemini API 연동), 검색 기능을 정의한다.
 */
public interface ChatService {

    /**
     * 사용자의 활성 채팅방 목록 조회.
     *
     * @param adminId 사용자 아이디
     * @return 최근 수정 순 채팅방 목록
     */
    List<ChatRoomVo> getRooms(String adminId);

    /**
     * 채팅방 검색.
     * 채팅방 제목 또는 메시지 내용에 키워드가 포함된 방을 검색한다.
     *
     * @param adminId 사용자 아이디
     * @param keyword 검색 키워드
     * @return 검색 결과 채팅방 목록
     */
    List<ChatRoomVo> searchRooms(String adminId, String keyword);

    /**
     * 메시지 전송 및 AI 응답 수신.
     * Gemini API를 호출하여 AI 답변을 받고 DB에 저장한다.
     * 첫 번째 메시지이면 채팅방 제목을 자동 생성한다.
     *
     * @param request 메시지 전송 요청 데이터
     * @return AI 응답, roomId, 신규 제목(해당 시)
     */
    ChatSendResponseVo sendMessage(ChatSendRequestVo request);

    /**
     * 특정 채팅방의 메시지 히스토리 조회.
     * 소유자 검증 후 시간순 전체 메시지를 반환한다.
     *
     * @param roomId  채팅방 ID
     * @param adminId 소유자 확인용 사용자 아이디
     * @return 시간순 메시지 목록
     */
    List<ChatMessageVo> getMessages(Long roomId, String adminId);

    /**
     * 채팅방 논리적 삭제.
     * use_yn을 'N'으로 변경한다.
     *
     * @param roomId  삭제할 채팅방 ID
     * @param adminId 소유자 확인용 사용자 아이디
     */
    void deleteRoom(Long roomId, String adminId);
}

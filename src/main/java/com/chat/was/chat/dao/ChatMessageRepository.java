package com.chat.was.chat.dao;

import com.chat.was.chat.vo.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 채팅 메시지 JPA Repository.
 * 대화 맥락 유지를 위한 최근 메시지 조회를 제공한다.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 채팅방의 메시지를 최신순으로 N개 조회한다.
     * Gemini API에 전달할 대화 컨텍스트(최근 5쌍 = 10개) 구성에 사용한다.
     *
     * @param roomId 채팅방 ID
     * @return 최신순 메시지 목록
     */
    List<ChatMessage> findTop10ByRoomIdOrderByCreatedAtDesc(Long roomId);

    /**
     * 특정 채팅방의 전체 메시지 수를 반환한다.
     * 첫 번째 메시지 여부 판단(채팅방 제목 자동 생성 트리거)에 사용한다.
     *
     * @param roomId 채팅방 ID
     * @return 메시지 수
     */
    long countByRoomId(Long roomId);

    /**
     * 특정 채팅방의 모든 메시지를 시간순으로 조회한다.
     * 채팅 히스토리 화면 표시에 사용한다.
     *
     * @param roomId 채팅방 ID
     * @return 시간순 메시지 목록
     */
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);
}

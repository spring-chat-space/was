package com.chat.was.chat.dao;

import com.chat.was.chat.vo.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 특정 채팅방의 모든 메시지를 물리 삭제한다.
     * 채팅방 삭제 시 연쇄 삭제에 사용한다. 단일 DELETE 쿼리로 처리된다.
     *
     * @param roomId 삭제할 채팅방 ID
     */
    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.roomId = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);
}

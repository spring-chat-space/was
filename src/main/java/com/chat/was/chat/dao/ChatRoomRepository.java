package com.chat.was.chat.dao;

import com.chat.was.chat.vo.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 JPA Repository.
 * 사용자별 채팅방 목록 조회 및 단건 조회를 제공한다.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 특정 사용자의 활성 채팅방 목록을 최근 수정 순으로 조회한다.
     *
     * @param adminId 조회할 사용자 아이디
     * @param useYn   활성 여부 ('Y': 활성)
     * @return 채팅방 목록 (최근 수정 순)
     */
    List<ChatRoom> findByAdminIdAndUseYnOrderByUpdatedAtDesc(String adminId, String useYn);

    /**
     * 특정 사용자의 채팅방 단건 조회 (논리적 삭제 필터링 포함).
     *
     * @param roomId  채팅방 ID
     * @param adminId 소유자 아이디
     * @param useYn   활성 여부
     * @return 채팅방 Optional
     */
    Optional<ChatRoom> findByRoomIdAndAdminIdAndUseYn(Long roomId, String adminId, String useYn);
}

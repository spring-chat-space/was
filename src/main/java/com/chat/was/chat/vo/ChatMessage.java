package com.chat.was.chat.vo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 JPA 엔티티.
 * chat_message 테이블과 매핑되며, USER/AI 양방향 메시지를 저장한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    /** 메시지 고유 ID (PK, AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    /** 소속 채팅방 ID (chat_room.room_id 참조) */
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /** 송신자 타입 ('USER' 또는 'AI') */
    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;

    /** 메시지 내용 */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 메시지 생성 일시 */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 채팅 메시지 생성 빌더.
     *
     * @param roomId     소속 채팅방 ID
     * @param senderType 송신자 타입 ('USER' 또는 'AI')
     * @param content    메시지 내용
     */
    @Builder
    public ChatMessage(Long roomId, String senderType, String content) {
        this.roomId = roomId;
        this.senderType = senderType;
        this.content = content;
    }
}

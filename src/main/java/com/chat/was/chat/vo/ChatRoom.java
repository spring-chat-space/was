package com.chat.was.chat.vo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 채팅방 JPA 엔티티.
 * chat_room 테이블과 매핑되며, 논리적 삭제(use_yn)를 지원한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_room")
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    /** 채팅방 고유 ID (PK, AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    /** 방 생성 사용자 시퀀스 PK (admin_user.user_seq 참조) */
    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    /** 채팅방 제목 (첫 메시지 전송 후 AI가 자동 생성) */
    @Column(name = "title", length = 200)
    private String title;

    /** 논리적 삭제 여부 ('Y': 정상, 'N': 삭제) */
    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    /** 채팅방 생성 일시 */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 채팅방 최종 수정 일시 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 채팅방 생성 빌더.
     *
     * @param userSeq 방 생성 사용자 시퀀스 PK
     */
    @Builder
    public ChatRoom(Long userSeq) {
        this.userSeq = userSeq;
        this.title = "새 채팅";
        this.useYn = "Y";
    }

    /**
     * 채팅방 제목 업데이트.
     * 첫 번째 메시지 전송 후 AI가 생성한 제목으로 갱신할 때 사용한다.
     *
     * @param title AI가 생성한 채팅방 제목
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * 채팅방 논리적 삭제.
     * use_yn을 'N'으로 변경하여 소프트 삭제 처리한다.
     */
    public void delete() {
        this.useYn = "N";
    }
}

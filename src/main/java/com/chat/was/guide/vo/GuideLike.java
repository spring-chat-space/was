package com.chat.was.guide.vo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 가이드 좋아요 JPA 엔티티.
 * (guide_seq, user_seq) UNIQUE 제약으로 중복 좋아요 방지.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "guide_like",
       uniqueConstraints = @UniqueConstraint(columnNames = {"guide_seq", "user_seq"}))
@EntityListeners(AuditingEntityListener.class)
public class GuideLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq")
    private Long likeSeq;

    @Column(name = "guide_seq", nullable = false)
    private Long guideSeq;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public GuideLike(Long guideSeq, Long userSeq) {
        this.guideSeq = guideSeq;
        this.userSeq = userSeq;
    }
}

package com.chat.was.common.comment.vo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 공통 댓글 JPA 엔티티.
 * domain_type + ref_id로 다른 도메인(guide 등)에서 재사용.
 * parent_seq가 NULL이면 최상위 댓글, 값이 있으면 1단계 대댓글.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq")
    private Long commentSeq;

    @Column(name = "domain_type", nullable = false, length = 50)
    private String domainType;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    /** NULL이면 최상위 댓글, 값이 있으면 대댓글 (1단계만 허용) */
    @Column(name = "parent_seq")
    private Long parentSeq;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Comment(String domainType, Long refId, Long userSeq, Long parentSeq, String content) {
        this.domainType = domainType;
        this.refId = refId;
        this.userSeq = userSeq;
        this.parentSeq = parentSeq;
        this.content = content;
        this.useYn = "Y";
    }

    /**
     * 댓글 내용 수정.
     *
     * @param content 새로운 내용
     */
    public void updateContent(String content) {
        this.content = content;
    }

    /**
     * 논리 삭제.
     */
    public void delete() {
        this.useYn = "N";
    }
}

package com.chat.was.guide.vo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 가이드 JPA 엔티티.
 * guide 테이블과 매핑. content는 NULLABLE (파일만 첨부하는 경우 빈 값 허용).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "guide")
@EntityListeners(AuditingEntityListener.class)
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_seq")
    private Long guideSeq;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    /** 첨부파일 그룹 (파일 없는 경우 NULL) */
    @Column(name = "file_group_seq")
    private Long fileGroupSeq;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 마크다운 원본 본문 (파일만 첨부 시 NULL 허용) */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public", nullable = false, length = 1)
    private String isPublic;

    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Guide(Long userSeq, Long fileGroupSeq, String title, String content, String isPublic) {
        this.userSeq = userSeq;
        this.fileGroupSeq = fileGroupSeq;
        this.title = title;
        this.content = content;
        this.isPublic = (isPublic != null) ? isPublic : "Y";
        this.useYn = "Y";
        this.likeCount = 0;
    }

    /**
     * 가이드 수정.
     *
     * @param title        새 제목
     * @param content      새 본문
     * @param isPublic     공개 여부
     * @param fileGroupSeq 첨부파일 그룹 시퀀스 (null이면 변경 없음)
     */
    public void update(String title, String content, String isPublic, Long fileGroupSeq) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        if (fileGroupSeq != null) {
            this.fileGroupSeq = fileGroupSeq;
        }
    }

    /**
     * 논리 삭제.
     */
    public void delete() {
        this.useYn = "N";
    }

    /**
     * 좋아요 수 증가.
     */
    public void incrementLike() {
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소.
     */
    public void decrementLike() {
        if (this.likeCount > 0) this.likeCount--;
    }
}

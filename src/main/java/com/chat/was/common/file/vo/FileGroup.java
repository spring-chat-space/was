package com.chat.was.common.file.vo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 공통 파일 그룹 JPA 엔티티.
 * file_group 테이블과 매핑. domain_type으로 다른 도메인(guide, board 등)에서 재사용.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file_group")
@EntityListeners(AuditingEntityListener.class)
public class FileGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_group_seq")
    private Long fileGroupSeq;

    /** 도메인 구분 (GUIDE, BOARD 등) */
    @Column(name = "group_type", nullable = false, length = 50)
    private String groupType;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public FileGroup(String groupType) {
        this.groupType = groupType;
    }
}

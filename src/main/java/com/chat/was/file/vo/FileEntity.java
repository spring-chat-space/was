package com.chat.was.file.vo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 공통 파일 JPA 엔티티.
 * file 테이블과 매핑. UUID 기반 파일명으로 서버에 저장.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file")
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_seq")
    private Long fileSeq;

    @Column(name = "file_group_seq", nullable = false)
    private Long fileGroupSeq;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_ext", nullable = false, length = 20)
    private String fileExt;

    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public FileEntity(Long fileGroupSeq, String originalName, String storedName,
                      String filePath, Long fileSize, String fileExt) {
        this.fileGroupSeq = fileGroupSeq;
        this.originalName = originalName;
        this.storedName = storedName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileExt = fileExt;
        this.useYn = "Y";
    }

    /**
     * 논리 삭제 처리.
     */
    public void delete() {
        this.useYn = "N";
    }
}

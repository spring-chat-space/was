package com.chat.was.common.file.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 파일 응답 VO.
 *
 * @param fileSeq       파일 시퀀스
 * @param fileGroupSeq  파일 그룹 시퀀스
 * @param originalName  원본 파일명
 * @param fileSize      파일 크기 (bytes)
 * @param fileExt       파일 확장자
 * @param createdAt     업로드 일시
 */
@Getter
@AllArgsConstructor
public class FileVo {
    private Long fileSeq;
    private Long fileGroupSeq;
    private String originalName;
    private Long fileSize;
    private String fileExt;
    private LocalDateTime createdAt;
}

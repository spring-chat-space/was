package com.chat.was.common.file.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/**
 * 파일 업로드 응답 VO.
 * 업로드 완료된 파일 그룹 시퀀스와 파일 목록을 반환.
 */
@Getter
@AllArgsConstructor
public class FileUploadResponseVo {
    /** 생성된 파일 그룹 시퀀스 */
    private Long fileGroupSeq;
    /** 업로드된 파일 목록 */
    private List<FileVo> files;
}

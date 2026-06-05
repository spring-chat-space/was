package com.chat.was.guide.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가이드 등록/수정 요청 VO.
 */
@Getter
@Setter
@NoArgsConstructor
public class GuideSaveRequestVo {

    @NotBlank(message = "관리자 아이디는 필수입니다.")
    private String adminId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;

    /** 본문 (null 허용 — 파일만 첨부 시) */
    private String content;

    /** 공개 여부 ('Y'/'N', 기본값 'Y') */
    private String isPublic = "Y";

    /** 첨부파일 그룹 시퀀스 (null이면 첨부파일 없음) */
    private Long fileGroupSeq;
}

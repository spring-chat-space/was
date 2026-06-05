package com.chat.was.comment.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 등록 요청 VO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentSaveRequestVo {

    @NotBlank(message = "관리자 아이디는 필수입니다.")
    private String adminId;

    @NotBlank(message = "도메인 타입은 필수입니다.")
    private String domainType;

    @NotNull(message = "참조 ID는 필수입니다.")
    private Long refId;

    /** null이면 최상위 댓글, 값이 있으면 대댓글 */
    private Long parentSeq;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}

package com.chat.was.comment.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 수정 요청 VO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateRequestVo {

    @NotBlank(message = "관리자 아이디는 필수입니다.")
    private String adminId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}

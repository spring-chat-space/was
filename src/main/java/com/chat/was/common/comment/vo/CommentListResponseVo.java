package com.chat.was.common.comment.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/**
 * 댓글 목록 응답 VO.
 */
@Getter
@AllArgsConstructor
public class CommentListResponseVo {
    /** 전체 댓글 + 대댓글 개수 합계 */
    private int totalCount;
    /** 댓글 목록 (트리 구조) */
    private List<CommentVo> comments;
}

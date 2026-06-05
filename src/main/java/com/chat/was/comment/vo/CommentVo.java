package com.chat.was.comment.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 응답 VO (트리 구조).
 * replies 리스트로 대댓글을 중첩 표현.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentVo {
    /** 댓글 시퀀스 */
    private Long commentSeq;
    /** 부모 댓글 시퀀스 (null이면 최상위 댓글) */
    private Long parentSeq;
    /** 작성자 이름 */
    private String authorName;
    /** 댓글 내용 */
    private String content;
    /** 생성 일시 */
    private LocalDateTime createdAt;
    /** 현재 요청자의 소유 여부 */
    private boolean isMine;
    /** 대댓글 목록 */
    private List<CommentVo> replies = new ArrayList<>();
}

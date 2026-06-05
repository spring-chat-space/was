package com.chat.was.comment.service;

import com.chat.was.comment.vo.*;

public interface CommentService {

    /**
     * 댓글 목록 조회 (트리 구조 — 최상위 댓글 + 대댓글).
     *
     * @param domainType 도메인 구분
     * @param refId      도메인 PK
     * @param adminId    현재 사용자 아이디
     * @return 댓글 목록 (트리 구조)
     */
    CommentListResponseVo getComments(String domainType, Long refId, String adminId);

    /**
     * 댓글 등록. 대댓글은 1단계만 허용 — parentSeq 댓글이 이미 대댓글이면 BusinessException.
     *
     * @param request 등록 요청 VO
     * @return 등록된 commentSeq
     */
    Long createComment(CommentSaveRequestVo request);

    /**
     * 댓글 수정 (본인만 가능).
     *
     * @param commentSeq 수정할 댓글 시퀀스
     * @param request    수정 요청 VO
     */
    void updateComment(Long commentSeq, CommentUpdateRequestVo request);

    /**
     * 댓글 삭제 — 논리 삭제. 최상위 댓글 삭제 시 대댓글도 함께 논리 삭제.
     *
     * @param commentSeq 삭제할 댓글 시퀀스
     * @param adminId    현재 사용자 아이디
     */
    void deleteComment(Long commentSeq, String adminId);
}

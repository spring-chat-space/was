package com.chat.was.common.comment.controller;

import com.chat.was.common.comment.service.CommentService;
import com.chat.was.common.comment.vo.*;
import com.chat.was.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공통 댓글 REST API 컨트롤러 (WAS).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    /**
     * 댓글 목록 조회.
     *
     * @param domainType 도메인 구분 (GUIDE 등)
     * @param refId      도메인 PK
     * @param adminId    현재 사용자 아이디
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CommentListResponseVo>> getComments(
            @RequestParam("domainType") String domainType,
            @RequestParam("refId") Long refId,
            @RequestParam("adminId") String adminId) {
        log.info("댓글 목록 조회 - domainType: {}, refId: {}", domainType, refId);
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments(domainType, refId, adminId)));
    }

    /**
     * 댓글 등록.
     *
     * @param request 등록 요청 VO
     * @return 등록된 commentSeq
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createComment(@Valid @RequestBody CommentSaveRequestVo request) {
        log.info("댓글 등록 - adminId: {}, domainType: {}, refId: {}",
                request.getAdminId(), request.getDomainType(), request.getRefId());
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(request)));
    }

    /**
     * 댓글 수정.
     *
     * @param commentSeq 수정할 댓글 시퀀스
     * @param request    수정 요청 VO
     */
    @PostMapping("/{commentSeq}/update")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable("commentSeq") Long commentSeq,
            @Valid @RequestBody CommentUpdateRequestVo request) {
        log.info("댓글 수정 - commentSeq: {}, adminId: {}", commentSeq, request.getAdminId());
        commentService.updateComment(commentSeq, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 댓글 삭제 (논리 삭제).
     *
     * @param commentSeq 삭제할 댓글 시퀀스
     * @param adminId    현재 사용자 아이디
     */
    @PostMapping("/{commentSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentSeq") Long commentSeq,
            @RequestParam("adminId") String adminId) {
        log.info("댓글 삭제 - commentSeq: {}, adminId: {}", commentSeq, adminId);
        commentService.deleteComment(commentSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

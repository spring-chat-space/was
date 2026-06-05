package com.chat.was.common.comment.service.impl;

import com.chat.was.auth.dao.AdminUserRepository;
import com.chat.was.common.comment.dao.CommentMapper;
import com.chat.was.common.comment.dao.CommentRepository;
import com.chat.was.common.comment.service.CommentService;
import com.chat.was.common.comment.vo.*;
import com.chat.was.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 댓글 비즈니스 로직 구현체.
 * 대댓글 1단계 제한, 부모 삭제 시 대댓글 연쇄 논리 삭제 처리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final AdminUserRepository adminUserRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public CommentListResponseVo getComments(String domainType, Long refId, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        // 최상위 댓글 조회
        List<CommentVo> roots = commentMapper.findRootComments(domainType, refId, userSeq);
        // 각 최상위 댓글에 대한 대댓글 조회
        for (CommentVo root : roots) {
            List<CommentVo> replies = commentMapper.findReplies(root.getCommentSeq(), userSeq);
            root.setReplies(replies);
        }
        // 전체 댓글 개수 (최상위 + 대댓글)
        int totalCount = roots.stream()
                .mapToInt(r -> 1 + r.getReplies().size())
                .sum();
        return new CommentListResponseVo(totalCount, roots);
    }

    @Override
    @Transactional
    public Long createComment(CommentSaveRequestVo request) {
        Long userSeq = resolveUserSeq(request.getAdminId());

        // 대댓글 등록 시 — 부모가 이미 대댓글이면 재대댓글 불허
        if (request.getParentSeq() != null) {
            Comment parent = commentRepository.findByCommentSeqAndUseYn(request.getParentSeq(), "Y")
                    .orElseThrow(() -> new BusinessException("부모 댓글을 찾을 수 없습니다."));
            if (parent.getParentSeq() != null) {
                throw new BusinessException("대댓글에는 답글을 달 수 없습니다.");
            }
        }

        Comment comment = commentRepository.save(Comment.builder()
                .domainType(request.getDomainType())
                .refId(request.getRefId())
                .userSeq(userSeq)
                .parentSeq(request.getParentSeq())
                .content(request.getContent())
                .build());

        log.info("댓글 등록 완료 - commentSeq: {}, adminId: {}", comment.getCommentSeq(), request.getAdminId());
        return comment.getCommentSeq();
    }

    @Override
    @Transactional
    public void updateComment(Long commentSeq, CommentUpdateRequestVo request) {
        Long userSeq = resolveUserSeq(request.getAdminId());
        Comment comment = commentRepository.findByCommentSeqAndUseYn(commentSeq, "Y")
                .orElseThrow(() -> new BusinessException("댓글을 찾을 수 없습니다."));
        if (!comment.getUserSeq().equals(userSeq)) {
            throw new BusinessException("수정 권한이 없습니다.");
        }
        comment.updateContent(request.getContent());
        commentRepository.save(comment);
        log.info("댓글 수정 완료 - commentSeq: {}", commentSeq);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentSeq, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        Comment comment = commentRepository.findByCommentSeqAndUseYn(commentSeq, "Y")
                .orElseThrow(() -> new BusinessException("댓글을 찾을 수 없습니다."));
        if (!comment.getUserSeq().equals(userSeq)) {
            throw new BusinessException("삭제 권한이 없습니다.");
        }

        // 최상위 댓글 삭제 시 대댓글도 함께 논리 삭제
        if (comment.getParentSeq() == null) {
            List<Comment> replies = commentRepository.findByParentSeqAndUseYn(commentSeq, "Y");
            replies.forEach(r -> {
                r.delete();
                commentRepository.save(r);
            });
        }

        comment.delete();
        commentRepository.save(comment);
        log.info("댓글 삭제 완료 - commentSeq: {}, adminId: {}", commentSeq, adminId);
    }

    /**
     * 관리자 아이디로 사용자 시퀀스 조회.
     *
     * @param adminId 관리자 아이디
     * @return 사용자 시퀀스
     */
    private Long resolveUserSeq(String adminId) {
        return adminUserRepository.findByAdminId(adminId)
                .map(u -> u.getUserSeq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + adminId));
    }
}

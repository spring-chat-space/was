package com.chat.was.common.comment.dao;

import com.chat.was.common.comment.vo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 댓글 시퀀스와 사용 여부로 댓글 조회.
     *
     * @param commentSeq 댓글 시퀀스
     * @param useYn      사용 여부 ('Y'만 조회)
     * @return 댓글 정보
     */
    Optional<Comment> findByCommentSeqAndUseYn(Long commentSeq, String useYn);

    /**
     * 대댓글 일괄 논리 삭제용 — 부모 댓글 삭제 시 호출.
     *
     * @param parentSeq 부모 댓글 시퀀스
     * @param useYn     사용 여부 ('Y'만 조회)
     * @return 대댓글 목록
     */
    List<Comment> findByParentSeqAndUseYn(Long parentSeq, String useYn);
}

package com.chat.was.comment.dao;

import com.chat.was.comment.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 댓글 목록 트리 구조 조회용 MyBatis 매퍼.
 */
@Mapper
public interface CommentMapper {

    /**
     * 최상위 댓글 목록 조회 (use_yn='Y', parent_seq IS NULL).
     *
     * @param domainType 도메인 구분
     * @param refId      도메인 PK
     * @param userSeq    현재 사용자 시퀀스 (isMine 판별)
     * @return 최상위 댓글 목록
     */
    List<CommentVo> findRootComments(
            @Param("domainType") String domainType,
            @Param("refId") Long refId,
            @Param("userSeq") Long userSeq);

    /**
     * 대댓글 목록 조회.
     *
     * @param parentSeq 부모 댓글 시퀀스
     * @param userSeq   현재 사용자 시퀀스
     * @return 대댓글 목록
     */
    List<CommentVo> findReplies(
            @Param("parentSeq") Long parentSeq,
            @Param("userSeq") Long userSeq);
}

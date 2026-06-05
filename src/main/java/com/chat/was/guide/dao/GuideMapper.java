package com.chat.was.guide.dao;

import com.chat.was.guide.vo.GuideListItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 가이드 목록 조회용 MyBatis 매퍼.
 * 복잡한 동적 쿼리(정렬, 검색, 접근 제어)를 처리한다.
 */
@Mapper
public interface GuideMapper {

    /**
     * 가이드 목록 조회 (동적 쿼리: sort=like|recent, keyword 검색, 접근 제어).
     *
     * @param userSeq 현재 사용자 시퀀스 (본인 비공개 가이드 포함 조건에 사용)
     * @param sort    정렬 방식 (like: 좋아요순, recent: 최신순)
     * @param keyword 검색 키워드 (null이면 전체 조회)
     * @return 가이드 목록 VO
     */
    List<GuideListItemVo> findGuideList(
            @Param("userSeq") Long userSeq,
            @Param("sort") String sort,
            @Param("keyword") String keyword);
}

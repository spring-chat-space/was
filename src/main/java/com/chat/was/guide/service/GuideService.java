package com.chat.was.guide.service;

import com.chat.was.guide.vo.*;
import java.util.List;

public interface GuideService {

    /**
     * 가이드 목록 조회.
     *
     * @param adminId 현재 사용자 아이디
     * @param sort    정렬 (like/recent)
     * @param keyword 검색 키워드
     * @return 가이드 목록
     */
    List<GuideListItemVo> getGuideList(String adminId, String sort, String keyword);

    /**
     * 가이드 상세 조회.
     * 비공개 가이드는 본인만 조회 가능.
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     * @return 가이드 상세 정보
     */
    GuideDetailVo getGuideDetail(Long guideSeq, String adminId);

    /**
     * 가이드 등록.
     *
     * @param request 등록 요청 VO
     * @return 등록된 가이드 시퀀스
     */
    Long createGuide(GuideSaveRequestVo request);

    /**
     * 가이드 수정 (본인만 가능).
     *
     * @param guideSeq 수정할 가이드 시퀀스
     * @param request  수정 요청 VO
     */
    void updateGuide(Long guideSeq, GuideSaveRequestVo request);

    /**
     * 가이드 삭제 — 논리 삭제 (본인만 가능).
     *
     * @param guideSeq 삭제할 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     */
    void deleteGuide(Long guideSeq, String adminId);

    /**
     * 좋아요 토글.
     * 없으면 추가, 있으면 취소. guide.like_count 동기화.
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     * @return 토글 후 좋아요 수와 현재 좋아요 여부
     */
    GuideLikeResponseVo toggleLike(Long guideSeq, String adminId);
}

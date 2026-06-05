package com.chat.was.guide.controller;

import com.chat.was.global.common.ApiResponse;
import com.chat.was.guide.service.GuideService;
import com.chat.was.guide.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 가이드 CRUD + 좋아요 REST API 컨트롤러 (WAS).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/guide")
@RequiredArgsConstructor
public class GuideApiController {

    private final GuideService guideService;

    /**
     * 가이드 목록 조회.
     *
     * @param adminId 현재 사용자 아이디
     * @param sort    정렬 (like/recent, 기본값: like)
     * @param keyword 검색 키워드 (선택사항)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GuideListItemVo>>> getGuideList(
            @RequestParam("adminId") String adminId,
            @RequestParam(value = "sort", defaultValue = "like") String sort,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("가이드 목록 조회 - adminId: {}, sort: {}, keyword: {}", adminId, sort, keyword);
        return ResponseEntity.ok(ApiResponse.success(guideService.getGuideList(adminId, sort, keyword)));
    }

    /**
     * 가이드 상세 조회.
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     */
    @GetMapping("/{guideSeq}")
    public ResponseEntity<ApiResponse<GuideDetailVo>> getGuideDetail(
            @PathVariable("guideSeq") Long guideSeq,
            @RequestParam("adminId") String adminId) {
        log.info("가이드 상세 조회 - guideSeq: {}, adminId: {}", guideSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success(guideService.getGuideDetail(guideSeq, adminId)));
    }

    /**
     * 가이드 등록.
     *
     * @param request 등록 요청 VO
     * @return 등록된 guideSeq
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createGuide(@Valid @RequestBody GuideSaveRequestVo request) {
        log.info("가이드 등록 - adminId: {}", request.getAdminId());
        return ResponseEntity.ok(ApiResponse.success(guideService.createGuide(request)));
    }

    /**
     * 가이드 수정.
     *
     * @param guideSeq 수정할 가이드 시퀀스
     * @param request  수정 요청 VO
     */
    @PostMapping("/{guideSeq}/update")
    public ResponseEntity<ApiResponse<Void>> updateGuide(
            @PathVariable("guideSeq") Long guideSeq,
            @Valid @RequestBody GuideSaveRequestVo request) {
        log.info("가이드 수정 - guideSeq: {}, adminId: {}", guideSeq, request.getAdminId());
        guideService.updateGuide(guideSeq, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 가이드 삭제 (논리 삭제).
     *
     * @param guideSeq 삭제할 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     */
    @PostMapping("/{guideSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteGuide(
            @PathVariable("guideSeq") Long guideSeq,
            @RequestParam("adminId") String adminId) {
        log.info("가이드 삭제 - guideSeq: {}, adminId: {}", guideSeq, adminId);
        guideService.deleteGuide(guideSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 좋아요 토글.
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  현재 사용자 아이디
     * @return 현재 좋아요 수와 좋아요 여부
     */
    @PostMapping("/{guideSeq}/like")
    public ResponseEntity<ApiResponse<GuideLikeResponseVo>> toggleLike(
            @PathVariable("guideSeq") Long guideSeq,
            @RequestParam("adminId") String adminId) {
        log.info("좋아요 토글 - guideSeq: {}, adminId: {}", guideSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success(guideService.toggleLike(guideSeq, adminId)));
    }
}

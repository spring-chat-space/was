package com.chat.was.guide.service.impl;

import com.chat.was.auth.dao.AdminUserRepository;
import com.chat.was.common.file.dao.FileRepository;
import com.chat.was.common.file.vo.FileVo;
import com.chat.was.global.exception.BusinessException;
import com.chat.was.guide.dao.GuideMapper;
import com.chat.was.guide.dao.GuideLikeRepository;
import com.chat.was.guide.dao.GuideRepository;
import com.chat.was.guide.service.GuideService;
import com.chat.was.guide.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 가이드 비즈니스 로직 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final AdminUserRepository adminUserRepository;
    private final GuideRepository guideRepository;
    private final GuideLikeRepository guideLikeRepository;
    private final GuideMapper guideMapper;
    private final FileRepository fileRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GuideListItemVo> getGuideList(String adminId, String sort, String keyword) {
        Long userSeq = resolveUserSeq(adminId);
        String sortParam = (sort == null || sort.isBlank()) ? "like" : sort;
        return guideMapper.findGuideList(userSeq, sortParam, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public GuideDetailVo getGuideDetail(Long guideSeq, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        Guide guide = guideRepository.findByGuideSeqAndUseYn(guideSeq, "Y")
                .orElseThrow(() -> new BusinessException("가이드를 찾을 수 없습니다."));

        // 비공개 가이드는 본인만 조회 가능
        if ("N".equals(guide.getIsPublic()) && !guide.getUserSeq().equals(userSeq)) {
            throw new BusinessException("접근 권한이 없습니다.");
        }

        String authorName = adminUserRepository.findById(guide.getUserSeq())
                .map(u -> u.getAdminName())
                .orElse("알 수 없음");

        boolean myLiked = guideLikeRepository.findByGuideSeqAndUserSeq(guideSeq, userSeq).isPresent();
        boolean isMine = guide.getUserSeq().equals(userSeq);

        // 첨부 파일 목록 조회
        List<FileVo> files = List.of();
        if (guide.getFileGroupSeq() != null) {
            files = fileRepository.findByFileGroupSeqAndUseYnOrderByCreatedAtAsc(guide.getFileGroupSeq(), "Y")
                    .stream()
                    .map(f -> new FileVo(f.getFileSeq(), f.getFileGroupSeq(), f.getOriginalName(),
                            f.getFileSize(), f.getFileExt(), f.getCreatedAt()))
                    .collect(Collectors.toList());
        }

        return new GuideDetailVo(guide.getGuideSeq(), guide.getUserSeq(), authorName,
                guide.getFileGroupSeq(), guide.getTitle(), guide.getContent(),
                guide.getIsPublic(), guide.getLikeCount(), myLiked, isMine,
                guide.getCreatedAt(), guide.getUpdatedAt(), files);
    }

    @Override
    @Transactional
    public Long createGuide(GuideSaveRequestVo request) {
        Long userSeq = resolveUserSeq(request.getAdminId());
        Guide guide = guideRepository.save(Guide.builder()
                .userSeq(userSeq)
                .fileGroupSeq(request.getFileGroupSeq())
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(request.getIsPublic())
                .build());
        log.info("가이드 등록 완료 - guideSeq: {}, adminId: {}", guide.getGuideSeq(), request.getAdminId());
        return guide.getGuideSeq();
    }

    @Override
    @Transactional
    public void updateGuide(Long guideSeq, GuideSaveRequestVo request) {
        Long userSeq = resolveUserSeq(request.getAdminId());
        Guide guide = guideRepository.findByGuideSeqAndUseYn(guideSeq, "Y")
                .orElseThrow(() -> new BusinessException("가이드를 찾을 수 없습니다."));
        if (!guide.getUserSeq().equals(userSeq)) {
            throw new BusinessException("수정 권한이 없습니다.");
        }
        guide.update(request.getTitle(), request.getContent(), request.getIsPublic(), request.getFileGroupSeq());
        guideRepository.save(guide);
        log.info("가이드 수정 완료 - guideSeq: {}", guideSeq);
    }

    @Override
    @Transactional
    public void deleteGuide(Long guideSeq, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        Guide guide = guideRepository.findByGuideSeqAndUseYn(guideSeq, "Y")
                .orElseThrow(() -> new BusinessException("가이드를 찾을 수 없습니다."));
        if (!guide.getUserSeq().equals(userSeq)) {
            throw new BusinessException("삭제 권한이 없습니다.");
        }
        guide.delete();
        guideRepository.save(guide);
        log.info("가이드 삭제 완료 - guideSeq: {}, adminId: {}", guideSeq, adminId);
    }

    @Override
    @Transactional
    public GuideLikeResponseVo toggleLike(Long guideSeq, String adminId) {
        Long userSeq = resolveUserSeq(adminId);
        Guide guide = guideRepository.findByGuideSeqAndUseYn(guideSeq, "Y")
                .orElseThrow(() -> new BusinessException("가이드를 찾을 수 없습니다."));

        // 예외 기반 토글 대신 선조회 후 분기하는 check-then-act 패턴 사용.
        // @Transactional 내에서 DataIntegrityViolationException을 catch하면
        // JDBC 레벨 트랜잭션이 오염되어 이후 쿼리가 모두 실패하므로 이 방식을 사용.
        Optional<GuideLike> existing = guideLikeRepository.findByGuideSeqAndUserSeq(guideSeq, userSeq);

        boolean liked;
        if (existing.isPresent()) {
            // 이미 좋아요 → 취소
            guideLikeRepository.delete(existing.get());
            guide.decrementLike();
            liked = false;
            log.info("좋아요 취소 - guideSeq: {}, userSeq: {}", guideSeq, userSeq);
        } else {
            // 좋아요 없음 → 추가
            guideLikeRepository.save(GuideLike.builder()
                    .guideSeq(guideSeq)
                    .userSeq(userSeq)
                    .build());
            guide.incrementLike();
            liked = true;
            log.info("좋아요 추가 - guideSeq: {}, userSeq: {}", guideSeq, userSeq);
        }
        guideRepository.save(guide);
        return new GuideLikeResponseVo(guide.getLikeCount(), liked);
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

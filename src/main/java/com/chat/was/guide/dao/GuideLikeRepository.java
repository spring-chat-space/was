package com.chat.was.guide.dao;

import com.chat.was.guide.vo.GuideLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GuideLikeRepository extends JpaRepository<GuideLike, Long> {
    /**
     * 가이드와 사용자의 좋아요 여부 조회.
     *
     * @param guideSeq 가이드 시퀀스
     * @param userSeq  사용자 시퀀스
     * @return 좋아요 정보
     */
    Optional<GuideLike> findByGuideSeqAndUserSeq(Long guideSeq, Long userSeq);
}

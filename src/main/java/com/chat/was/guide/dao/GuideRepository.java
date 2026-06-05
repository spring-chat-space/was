package com.chat.was.guide.dao;

import com.chat.was.guide.vo.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    /**
     * 가이드 시퀀스와 사용 여부로 가이드 조회.
     *
     * @param guideSeq 가이드 시퀀스
     * @param useYn    사용 여부 ('Y'만 조회)
     * @return 가이드 정보
     */
    Optional<Guide> findByGuideSeqAndUseYn(Long guideSeq, String useYn);
}

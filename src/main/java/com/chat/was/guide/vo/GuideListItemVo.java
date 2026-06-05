package com.chat.was.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 가이드 목록 조회 응답 VO (MyBatis 매핑용).
 */
@Getter
@Setter
@NoArgsConstructor
public class GuideListItemVo {
    /** 가이드 시퀀스 */
    private Long guideSeq;
    /** 작성자 사용자 시퀀스 */
    private Long userSeq;
    /** 가이드 제목 */
    private String title;
    /** 공개 여부 ('Y'/'N') */
    private String isPublic;
    /** 좋아요 수 */
    private Integer likeCount;
    /** 작성자 이름 */
    private String authorName;
    /** 생성 일시 */
    private LocalDateTime createdAt;
    /** 수정 일시 */
    private LocalDateTime updatedAt;
    /** 현재 요청자의 좋아요 여부 */
    private boolean myLiked;
}

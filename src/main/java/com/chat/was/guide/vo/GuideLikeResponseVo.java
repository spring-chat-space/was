package com.chat.was.guide.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 좋아요 토글 응답 VO.
 */
@Getter
@AllArgsConstructor
public class GuideLikeResponseVo {
    /** 현재 좋아요 수 */
    private Integer likeCount;
    /** 현재 요청자의 좋아요 여부 */
    private boolean liked;
}

package com.chat.was.guide.vo;

import com.chat.was.file.vo.FileVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 가이드 상세 조회 응답 VO.
 */
@Getter
@AllArgsConstructor
public class GuideDetailVo {
    /** 가이드 시퀀스 */
    private Long guideSeq;
    /** 작성자 사용자 시퀀스 */
    private Long userSeq;
    /** 작성자 이름 */
    private String authorName;
    /** 첨부파일 그룹 시퀀스 */
    private Long fileGroupSeq;
    /** 가이드 제목 */
    private String title;
    /** 마크다운 본문 */
    private String content;
    /** 공개 여부 ('Y'/'N') */
    private String isPublic;
    /** 좋아요 수 */
    private Integer likeCount;
    /** 현재 요청자의 좋아요 여부 */
    private boolean myLiked;
    /** 현재 요청자의 소유 여부 */
    private boolean isMine;
    /** 생성 일시 */
    private LocalDateTime createdAt;
    /** 수정 일시 */
    private LocalDateTime updatedAt;
    /** 첨부 파일 목록 */
    private List<FileVo> files;
}

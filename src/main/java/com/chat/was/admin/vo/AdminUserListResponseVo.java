package com.chat.was.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 사용자 목록 조회 페이징 응답 VO.
 * 목록 데이터와 페이징 메타 정보를 함께 담는다.
 */
@Getter
@AllArgsConstructor
public class AdminUserListResponseVo {

    /** 현재 페이지 사용자 목록 */
    private List<AdminUserListItemVo> items;

    /** 전체 조회 건수 */
    private long totalCount;

    /** 전체 페이지 수 */
    private int totalPages;

    /** 현재 페이지 번호 (0부터 시작) */
    private int currentPage;

    /** 페이지당 건수 */
    private int size;
}

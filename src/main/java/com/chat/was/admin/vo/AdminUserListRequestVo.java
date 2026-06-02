package com.chat.was.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 목록 조회 요청 파라미터 VO.
 * 키워드·권한·상태 필터와 페이징 옵션을 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminUserListRequestVo {

    /** 아이디 또는 이름 키워드 (부분 일치, 선택) */
    private String keyword;

    /** 권한 필터: ROLE_ADMIN | ROLE_USER (선택, null이면 전체) */
    private String role;

    /**
     * 계정 상태 필터 (선택, null이면 전체).
     * active   : use_yn = 'Y' AND (locked_until IS NULL OR locked_until <= NOW())
     * inactive : use_yn = 'N'
     * locked   : locked_until IS NOT NULL AND locked_until > NOW()
     */
    private String status;

    /** 페이지 번호 (0부터 시작, 기본값 0) */
    private int page = 0;

    /** 페이지당 건수 (5 | 10 | 30 | 50, 기본값 10) */
    private int size = 10;

    /** MyBatis LIMIT/OFFSET용 offset 계산값 */
    public int getOffset() {
        return page * size;
    }
}

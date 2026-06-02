package com.chat.was.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 목록 조회 결과 단건 VO.
 * MyBatis가 admin_user 테이블 행을 이 객체로 매핑한다.
 * email, phone_number는 AES256Converter TypeHandler로 자동 복호화한다.
 */
@Getter
@NoArgsConstructor
public class AdminUserListItemVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 관리자 이름 */
    private String adminName;

    /** 이메일 (복호화 값) */
    private String email;

    /** 전화번호 (복호화 값) */
    private String phoneNumber;

    /** 권한 (ROLE_USER / ROLE_ADMIN) */
    private String role;

    /** 계정 활성화 여부 (Y / N) */
    private String useYn;

    /** 계정 잠금 해제 시간 (null이면 잠금 없음) */
    private LocalDateTime lockedUntil;

    /** 가입일 */
    private LocalDateTime createdAt;

    /**
     * 화면 표시용 계정 상태 문자열 반환.
     * locked > inactive > active 순서로 판정한다.
     *
     * @return "locked" | "inactive" | "active"
     */
    public String getStatus() {
        if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
            return "locked";
        }
        if (!"Y".equals(useYn)) {
            return "inactive";
        }
        return "active";
    }
}

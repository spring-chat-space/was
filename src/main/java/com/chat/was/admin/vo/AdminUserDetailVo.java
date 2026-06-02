package com.chat.was.admin.vo;

import com.chat.was.auth.vo.AdminUser;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 단건 상세 조회 응답 VO.
 * AdminUser 엔티티의 민감 정보(비밀번호 제외)를 모두 포함한다.
 */
@Getter
public class AdminUserDetailVo {

    private final String adminId;
    private final String adminName;
    private final String email;
    private final String phoneNumber;
    private final String role;
    private final String useYn;
    private final int loginFailCount;
    private final LocalDateTime lockedUntil;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * AdminUser 엔티티로부터 상세 조회 VO를 생성한다.
     *
     * @param adminUser 조회된 AdminUser 엔티티
     */
    public AdminUserDetailVo(AdminUser adminUser) {
        this.adminId = adminUser.getAdminId();
        this.adminName = adminUser.getAdminName();
        this.email = adminUser.getEmail();
        this.phoneNumber = adminUser.getPhoneNumber();
        this.role = adminUser.getRole();
        this.useYn = adminUser.getUseYn();
        this.loginFailCount = adminUser.getLoginFailCount();
        this.lockedUntil = adminUser.getLockedUntil();
        this.createdAt = adminUser.getCreatedAt();
        this.updatedAt = adminUser.getUpdatedAt();
    }

    /**
     * 화면 표시용 계정 상태 문자열 반환.
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

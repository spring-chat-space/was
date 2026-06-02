package com.chat.was.admin.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 신규 생성 요청 VO.
 * 관리자가 새 계정을 생성할 때 전달되는 요청 바디.
 */
@Getter
@NoArgsConstructor
public class AdminUserCreateRequestVo {

    /**
     * 생성할 관리자 아이디.
     * 3~50자 영문, 숫자, 언더스코어 조합.
     */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 3, max = 50, message = "아이디는 3~50자 범위여야 합니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "아이디는 영문, 숫자, 언더스코어만 사용 가능합니다."
    )
    private String adminId;

    /**
     * 비밀번호 (BCrypt 암호화 저장됨).
     * 8자 이상 필수, 대문자/소문자/숫자/특수문자 포함 권장.
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    /**
     * 관리자 이름.
     */
    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String adminName;

    /**
     * 이메일 (AES-256 암호화 저장됨).
     */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    /**
     * 전화번호 (AES-256 암호화 저장됨, 선택).
     * 입력되지 않으면 빈 문자열로 처리.
     */
    private String phoneNumber;

    /**
     * 사용자 권한 (선택, 기본값: ROLE_USER).
     * ROLE_USER 또는 ROLE_ADMIN만 허용.
     */
    @Pattern(
        regexp = "^(ROLE_USER|ROLE_ADMIN)?$",
        message = "권한 값은 ROLE_USER 또는 ROLE_ADMIN이어야 합니다."
    )
    private String role;
}

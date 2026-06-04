package com.chat.was.auth.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 데이터 전송 객체.
 * WEB 레이어에서 POST /api/v1/auth/signup 호출 시 전달되는 요청 바디.
 * Bean Validation으로 필수값 및 형식 검증을 수행한다.
 */
@Getter
@NoArgsConstructor
public class SignupRequestVo {

    /** 관리자 아이디 */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String adminId;

    /**
     * 평문 비밀번호 (서비스 계층에서 BCrypt 암호화 처리).
     * 8자 이상, 대문자·소문자·숫자·특수문자(!@#$%^&* 등) 각 1개 이상 포함, 공백 불가.
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
        message = "비밀번호는 8자 이상이며 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    private String password;

    /** 관리자 이름 */
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String adminName;

    /** 이메일 (서비스 계층에서 AES-256 암호화 후 저장) */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    /**
     * 전화번호 (선택 입력, 서비스 계층에서 AES-256 암호화 후 저장).
     * 입력된 경우에만 @Pattern 형식 검증이 적용된다 (null/빈 값은 통과).
     */
    @Pattern(
        regexp = "^$|^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
        message = "올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)"
    )
    private String phoneNumber;
}

package com.chat.was.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 데이터 전송 객체.
 * WEB 레이어에서 POST /api/v1/auth/login 호출 시 전달되는 요청 바디.
 */
@Getter
@NoArgsConstructor
public class LoginRequestVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 평문 비밀번호 (서비스 계층에서 BCrypt 검증) */
    private String password;
}

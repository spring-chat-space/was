package com.chat.was.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 응답 데이터 전송 객체.
 * WAS 로그인 API 성공 시 WEB 레이어로 반환되는 사용자 정보.
 * WEB 레이어는 이 정보를 Spring Security 컨텍스트 및 HttpSession에 저장한다.
 */
@Getter
@AllArgsConstructor
public class LoginResponseVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 관리자 이름 */
    private String adminName;

    /** 복호화된 이메일 */
    private String email;

    /** 사용자 권한 (ROLE_USER, ROLE_ADMIN) */
    private String role;
}

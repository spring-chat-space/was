package com.chat.was.auth.service;

import com.chat.was.auth.vo.LoginRequestVo;
import com.chat.was.auth.vo.LoginResponseVo;
import com.chat.was.auth.vo.SignupRequestVo;

/**
 * 인증 비즈니스 로직 인터페이스.
 * 회원가입 및 로그인 처리를 정의한다.
 */
public interface AuthService {

    /**
     * 회원가입 처리.
     * 아이디 중복 검사 후 비밀번호를 BCrypt로 암호화하여 저장한다.
     *
     * @param signupRequestVo 회원가입 요청 데이터
     * @throws IllegalArgumentException 아이디 중복 시
     */
    void signup(SignupRequestVo signupRequestVo);

    /**
     * 로그인 처리.
     * 아이디 존재 여부, use_yn 상태, BCrypt 비밀번호 검증을 순서대로 수행한다.
     *
     * @param loginRequestVo 로그인 요청 데이터
     * @return 로그인 성공 시 사용자 정보 (LoginResponseVo)
     * @throws IllegalArgumentException 인증 실패 또는 계정 비활성화 시
     */
    LoginResponseVo login(LoginRequestVo loginRequestVo);
}

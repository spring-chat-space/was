package com.chat.was.auth.controller;

import com.chat.was.auth.service.AuthService;
import com.chat.was.auth.vo.LoginRequestVo;
import com.chat.was.auth.vo.LoginResponseVo;
import com.chat.was.auth.vo.SignupRequestVo;
import com.chat.was.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST API 컨트롤러.
 * 회원가입 및 로그인 요청을 처리하여 ApiResponse 형태로 반환한다.
 * 예외 처리는 GlobalExceptionHandler에서 일괄 처리한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    /**
     * 회원가입 API.
     * 아이디 중복 검사 후 신규 관리자 계정을 생성한다.
     *
     * @param signupRequestVo 회원가입 요청 데이터 (adminId, password, adminName, email, phoneNumber)
     * @return 성공 시 200 OK, ApiResponse (data=null)
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestVo signupRequestVo) {
        log.info("회원가입 API 요청 - ID: {}", signupRequestVo.getAdminId());
        authService.signup(signupRequestVo);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 로그인 API.
     * 아이디/비밀번호 검증 및 계정 활성화 상태 확인 후 사용자 정보를 반환한다.
     *
     * @param loginRequestVo 로그인 요청 데이터 (adminId, password)
     * @return 성공 시 200 OK, ApiResponse (data=LoginResponseVo)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseVo>> login(@RequestBody LoginRequestVo loginRequestVo) {
        log.info("로그인 API 요청 - ID: {}", loginRequestVo.getAdminId());
        LoginResponseVo loginResponseVo = authService.login(loginRequestVo);
        return ResponseEntity.ok(ApiResponse.success(loginResponseVo));
    }
}

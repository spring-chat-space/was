package com.chat.was.auth.service.impl;

import com.chat.was.auth.dao.AdminUserRepository;
import com.chat.was.auth.service.AuthService;
import com.chat.was.auth.vo.AdminUser;
import com.chat.was.auth.vo.LoginRequestVo;
import com.chat.was.auth.vo.LoginResponseVo;
import com.chat.was.auth.vo.SignupRequestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 인증 비즈니스 로직 구현체.
 * 회원가입 시 BCrypt 암호화, 로그인 시 아이디/비밀번호/계정 상태 검증을 처리한다.
 * 5회 연속 로그인 실패 시 1시간 계정 잠금 로직을 포함한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리.
     * 아이디 중복 검사 → 비밀번호 BCrypt 암호화 → 저장 순서로 처리한다.
     *
     * @param signupRequestVo 회원가입 요청 데이터
     * @throws IllegalArgumentException 아이디 중복 시
     */
    @Override
    @Transactional
    public void signup(SignupRequestVo signupRequestVo) {
        log.info("회원가입 시도 - ID: {}", signupRequestVo.getAdminId());

        if (adminUserRepository.existsByAdminId(signupRequestVo.getAdminId())) {
            log.error("회원가입 실패 - 중복된 아이디: {}", signupRequestVo.getAdminId());
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 평문 비밀번호를 BCrypt로 단방향 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestVo.getPassword());

        AdminUser adminUser = AdminUser.builder()
                .adminId(signupRequestVo.getAdminId())
                .password(encodedPassword)
                .adminName(signupRequestVo.getAdminName())
                .email(signupRequestVo.getEmail())
                .phoneNumber(signupRequestVo.getPhoneNumber())
                .build();

        adminUserRepository.save(adminUser);
        log.info("회원가입 완료 - ID: {}", signupRequestVo.getAdminId());
    }

    /**
     * 로그인 처리.
     * 아이디 존재 여부 → 계정 잠금 상태 → 계정 활성화 상태(use_yn) → BCrypt 비밀번호 검증 순서로 수행한다.
     * 로그인 실패 시 login_fail_count를 증가시키고 5회 달성 시 1시간 잠금 처리한다.
     * 로그인 성공 시 fail count와 lock 상태를 초기화한다.
     *
     * @param loginRequestVo 로그인 요청 데이터
     * @return 로그인 성공 시 사용자 정보 (LoginResponseVo)
     * @throws IllegalArgumentException 인증 실패, 계정 비활성화, 계정 잠금 시
     */
    @Override
    @Transactional
    public LoginResponseVo login(LoginRequestVo loginRequestVo) {
        log.info("로그인 시도 - ID: {}", loginRequestVo.getAdminId());

        AdminUser adminUser = adminUserRepository.findByAdminId(loginRequestVo.getAdminId())
                .orElseThrow(() -> {
                    log.error("로그인 실패 - 존재하지 않는 아이디: {}", loginRequestVo.getAdminId());
                    return new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
                });

        // 계정 잠금 상태 체크: locked_until이 현재 시간보다 미래면 잠금 중
        if (adminUser.getLockedUntil() != null && adminUser.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.error("로그인 실패 - 잠금된 계정: {}", loginRequestVo.getAdminId());
            throw new IllegalArgumentException("계정이 1시간 동안 잠겼습니다.");
        }

        // 계정 비활성화 상태 체크 (use_yn = 'N')
        if (!"Y".equals(adminUser.getUseYn())) {
            log.error("로그인 실패 - 비활성화된 계정: {}", loginRequestVo.getAdminId());
            throw new IllegalArgumentException("사용이 중지된 계정입니다. 관리자에게 문의하세요.");
        }

        // BCrypt 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestVo.getPassword(), adminUser.getPassword())) {
            log.error("로그인 실패 - 비밀번호 불일치: {}", loginRequestVo.getAdminId());
            // 실패 횟수 증가 (5회 달성 시 lockedUntil 자동 설정)
            adminUser.increaseLoginFailCount();
            adminUserRepository.save(adminUser);
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공: 실패 카운터 및 잠금 상태 초기화
        adminUser.resetLoginState();
        adminUserRepository.save(adminUser);

        log.info("로그인 성공 - ID: {}", loginRequestVo.getAdminId());
        return new LoginResponseVo(
                adminUser.getAdminId(),
                adminUser.getAdminName(),
                adminUser.getEmail(),
                adminUser.getRole()
        );
    }
}

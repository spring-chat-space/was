package com.chat.was.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WAS 레이어 Spring Security 설정.
 * WAS는 REST API 서버이므로 모든 요청을 허용하고, PasswordEncoder Bean만 제공한다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt 단방향 암호화 인코더 Bean.
     * 회원가입 시 비밀번호 암호화 및 로그인 시 검증에 사용.
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * WAS는 내부 API 서버이므로 모든 요청 허용.
     * 인증/인가는 WEB 레이어에서 담당.
     *
     * @param http HttpSecurity 설정 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 오류 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}

package com.chat.was.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정 클래스.
 * createdAt, updatedAt 등 감사(Auditing) 필드 자동 관리를 위해 @EnableJpaAuditing 적용.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

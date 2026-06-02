package com.chat.was.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정 클래스.
 * - @Mapper 애노테이션이 명시된 인터페이스만 스캔하여 JPA Repository와의 Bean 충돌을 방지한다.
 */
@Configuration
@MapperScan(basePackages = "com.chat.was.**.dao", annotationClass = Mapper.class)
public class MyBatisConfig {
}

package com.chat.was.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 스캔 설정 클래스.
 * annotationClass = Mapper.class 필터를 통해 @Mapper 애노테이션이 명시된 인터페이스만 스캔.
 * JPA Repository 인터페이스(@Mapper 없음)와의 Bean 충돌을 방지하기 위해 반드시 필터가 필요하다.
 */
@Configuration
@MapperScan(basePackages = "com.chat.was.**.dao", annotationClass = Mapper.class)
public class MyBatisConfig {
}

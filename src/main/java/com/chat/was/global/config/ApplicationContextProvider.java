package com.chat.was.global.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationContext를 static하게 접근할 수 있게 해주는 유틸리티 클래스.
 * MyBatis TypeHandler처럼 Spring 컨테이너의 라이프사이클 밖에서
 * 리플렉션으로 생성되는 객체가 Spring Bean을 조회할 때 사용한다.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 클래스 타입으로 Spring Bean을 조회합니다.
     *
     * @param beanClass 조회할 클래스 타입
     * @param <T>       Bean 타입
     * @return 주입된 Bean 인스턴스
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext가 아직 초기화되지 않았습니다.");
        }
        return applicationContext.getBean(beanClass);
    }
}

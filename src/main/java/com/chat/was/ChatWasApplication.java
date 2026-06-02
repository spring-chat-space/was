package com.chat.was;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * chat-was 애플리케이션 진입점.
 * REST API 제공, 핵심 비즈니스 로직 처리, 영속성(DB) 관리 담당 레이어.
 */
@SpringBootApplication
public class ChatWasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatWasApplication.class, args);
    }
}

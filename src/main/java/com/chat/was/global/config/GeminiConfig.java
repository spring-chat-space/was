package com.chat.was.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Google Gemini API 연동용 RestClient 설정.
 * Gemini 2.5 Flash 모델 호출에 사용하는 전용 RestClient Bean을 등록한다.
 */
@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Gemini API 호출용 RestClient Bean.
     * baseUrl은 Gemini generativelanguage 엔드포인트로 고정하고,
     * API 키는 쿼리 파라미터로 전달하므로 여기서는 URL만 설정한다.
     *
     * @return 설정된 RestClient 인스턴스
     */
    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Gemini API 키 반환.
     * ChatServiceImpl에서 쿼리 파라미터 구성 시 사용한다.
     *
     * @return API 키 문자열
     */
    public String getGeminiApiKey() {
        return geminiApiKey;
    }
}

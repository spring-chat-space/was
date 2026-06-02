package com.chat.was.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WAS API 공통 응답 래퍼 클래스.
 * 모든 REST API 응답은 이 형태로 반환하여 클라이언트(WEB 레이어)가 일관된 포맷으로 처리할 수 있도록 한다.
 *
 * @param <T> 실제 응답 데이터 타입
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    /** 성공 여부 */
    private boolean success;

    /** 결과 메시지 (성공/실패 여부 설명) */
    private String message;

    /** 실제 반환 데이터 객체 */
    private T result;

    /**
     * 성공 응답 생성 (데이터 포함).
     *
     * @param result 반환할 데이터
     * @param <T>    데이터 타입
     * @return success=true, message="success", result=result 형태의 응답
     */
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, "success", result);
    }

    /**
     * 성공 응답 생성 (데이터 없음).
     *
     * @return success=true, message="success", result=null 형태의 응답
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "success", null);
    }

    /**
     * 에러 응답 생성.
     *
     * @param message 에러 메시지
     * @param <T>     데이터 타입
     * @return success=false, 에러 메시지를 담은 응답 (result=null)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

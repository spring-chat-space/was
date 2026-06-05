package com.chat.was.global.exception;

/**
 * 비즈니스 규칙 위반 시 발생하는 커스텀 예외.
 * GlobalExceptionHandler에서 400 Bad Request로 처리된다.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

package com.chat.was.global.exception;

import com.chat.was.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

/**
 * WAS 전역 예외 처리 클래스.
 * 모든 컨트롤러에서 발생하는 예외를 낚아채어 일관된 ApiResponse 에러 포맷으로 반환한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation 에러 처리 (@Valid 검증 실패 시 발생).
     * 모든 필드 에러 메시지를 쉼표로 연결하여 반환한다.
     *
     * @param e 발생한 검증 예외
     * @return 400 상태코드와 검증 에러 메시지를 담은 ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("유효성 검증 오류 발생: {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /**
     * 비즈니스 로직 오류 처리 (중복 ID, 잘못된 인증 정보 등).
     * IllegalArgumentException 발생 시 400 Bad Request로 응답.
     *
     * @param e 발생한 예외
     * @return 400 상태코드와 에러 메시지를 담은 ApiResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("비즈니스 로직 오류 발생: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 비즈니스 규칙 위반 처리.
     * BusinessException 발생 시 400 Bad Request로 응답.
     *
     * @param e 발생한 예외
     * @return 400 상태코드와 에러 메시지를 담은 ApiResponse
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("비즈니스 규칙 위반: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 파일 크기 초과 처리.
     * MaxUploadSizeExceededException 발생 시 413 Payload Too Large로 응답.
     *
     * @param e 발생한 예외
     * @return 413 상태코드와 에러 메시지를 담은 ApiResponse
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("파일 크기 초과: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("파일 크기가 허용 범위를 초과했습니다."));
    }

    /**
     * 시스템 예외 처리 (예상치 못한 서버 오류).
     * Exception 발생 시 500 Internal Server Error로 응답.
     *
     * @param e 발생한 예외
     * @return 500 상태코드와 에러 메시지를 담은 ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("시스템 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}

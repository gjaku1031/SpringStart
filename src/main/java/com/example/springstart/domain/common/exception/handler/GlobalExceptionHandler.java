package com.example.springstart.domain.common.exception.handler;

import com.example.springstart.domain.common.exception.dto.ApiErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 * - `@RestControllerAdvice`를 사용하여 전역적으로 발생하는 예외를 처리
 * - 프로젝트 전반에서 발생하는 다양한 예외를 JSON 응답으로 반환하여 클라이언트가 일관된 에러 응답을 받을 수 있도록 함
 * - 예외 발생 시 상세한 로그를 남겨 디버깅이 용이하도록 함
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DTO 검증 실패 예외 처리 (MethodArgumentNotValidException)
     * - `@Valid` 또는 `@Validated` 사용 시 유효성 검사 실패하면 발생하는 예외
     * - 유효성 검사 실패한 필드와 메시지를 JSON 응답으로 반환
     *
     * @param e MethodArgumentNotValidException 예외 객체
     * @return ResponseEntity<ApiErrorResponseDto> JSON 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleException(MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();

        // 예외 메시지를 로그로 기록
        log.error("MethodArgumentNotValidException 발생: {}", e.getMessage());

        // 유효성 검사 실패한 필드와 메시지를 문자열로 조합
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors
                    .append(fieldError.getField())   // 오류 발생 필드명
                    .append(" (")
                    .append(fieldError.getDefaultMessage()) // 오류 메시지
                    .append("), ");
        }

        // 마지막 쉼표 제거
        if (errors.length() > 0) {
            errors.replace(errors.lastIndexOf(","), errors.length(), "");
        }

        return new ResponseEntity<>(
                new ApiErrorResponseDto(
                        HttpStatus.BAD_REQUEST.value(),  // HTTP 상태 코드 (400)
                        HttpStatus.BAD_REQUEST.name(),   // 상태 이름 ("BAD_REQUEST")
                        errors.toString()               // 조합된 오류 메시지
                ),
                HttpStatus.BAD_REQUEST  // ResponseEntity의 상태 코드 설정
        );
    }

    /**
     * 최상위 예외 처리 (Exception)
     * - 위에서 처리하지 못한 모든 예외를 최종적으로 처리하는 메서드
     * - 예기치 못한 예외가 발생할 경우 `500 Internal Server Error` 상태 코드와 함께 JSON 응답 반환
     *
     * @param e Exception 예외 객체
     * @return ResponseEntity<ApiErrorResponseDto> JSON 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleException(Exception e) {

        // 예외 메시지를 로그로 기록
        log.error("🔥 전역 예외(Global Exception) 발생: {}", e.getMessage());

        return new ResponseEntity<>(
                new ApiErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),  // HTTP 상태 코드 (500)
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),   // 상태 이름 ("INTERNAL_SERVER_ERROR")
                        e.getMessage()  // 예외 메시지 (보안상 필요하면 변경 가능)
                ),
                HttpStatus.INTERNAL_SERVER_ERROR  // ResponseEntity의 상태 코드 설정
        );
    }
}
/*

     * 사용자 정의 예외 (UniversityException) 처리
     * - 개발자가 `throw new UniversityException(...)` 형태로 발생시킨 예외를 처리하는 메서드
     * - HTTP 상태 코드, 에러 유형(type), 메시지를 `ApiErrorResponseDto`에 담아 JSON으로 응답
     *
     * @param e UniversityException 예외 객체
     * @return ResponseEntity<ApiErrorResponseDto> JSON 응답

@ExceptionHandler(UniversityException.class)
public ResponseEntity<ApiErrorResponseDto> handleException(UniversityException e) {

    // 예외 메시지를 로그로 기록
    log.error("⚠️ UniversityException 발생: {}", e.getMessage());

    return new ResponseEntity<>(
            new ApiErrorResponseDto(
                    e.getStatus().value(),  // HTTP 상태 코드
                    e.getType(),            // 예외 유형 (커스텀)
                    e.getMessage()          // 상세 메시지
            ),
            e.getStatus()  // ResponseEntity의 상태 코드 설정
    );
}
 */

package com.grow.matching_service.matching.presentation.exception;

import com.grow.matching_service.matching.domain.exception.InvalidMatchingParameterException;
import com.grow.matching_service.matching.application.exception.MatchingNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 검증 실패 처리 (@RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest request) {
        log.error("[Validation ERROR] 메서드 파라미터 유효성 검증 실패: {}", e.getMessage());

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_FAILED")
                .message("입력값 검증에 실패했습니다.")
                .error(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 도메인 커스텀 예외 처리 (InvalidMatchingParameterException)
    @ExceptionHandler(InvalidMatchingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidMatchingParameterException(InvalidMatchingParameterException ex,
                                                                 HttpServletRequest request) {
        log.error("[Domain ERROR] 매칭 파라미터 오류: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ex.getErrorCode().toString()) // 각 코드를 불러옴
                .message(ex.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 서비스 커스텀 예외 처리 (MatchingNotFoundException)
    @ExceptionHandler(MatchingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMatchingNotFoundException(MatchingNotFoundException ex,
                                                         HttpServletRequest request) {
        log.error("[Domain ERROR] 매칭 정보 없음: {}", ex.getMessage());
        return ErrorResponse.builder()
                .error(List.of("[Domain Error] 매칭 정보 없음: "))
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode().toString())
                .message(ex.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 일반 예외 fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("[SERVER ERROR] 예기치 못한 서버 오류: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_ERROR")
                .message("예기치 못한 서버 오류가 발생했습니다.")
                .error(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
package com.grow.matching_service.matching.presentation.exception;

import com.grow.matching_service.matching.application.exception.AccessDeniedException;
import com.grow.matching_service.matching.application.exception.AlreadyDeletedException;
import com.grow.matching_service.matching.domain.exception.MatchingLimitExceededException;
import com.grow.matching_service.matching.domain.exception.InvalidMatchingParameterException;
import com.grow.matching_service.matching.application.exception.MatchingNotFoundException;
import jakarta.persistence.OptimisticLockException;
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
        log.error("[Domain ERROR] 매칭 파라미터 오류: {}", ex.getErrorCode().getMessage());
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
        log.error("[Domain ERROR] 매칭 정보 없음: {}", ex.getErrorCode().getMessage());
        return ErrorResponse.builder()
                .error(List.of("[Domain Error] 매칭 정보 없음"))
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode().toString())
                .message(ex.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 도메인 커스텀 예외 처리 (AccessDeniedException)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex,
                                                     HttpServletRequest request) {
        log.error("[Domain ERROR] 권한 없음: {}", ex.getErrorCode().getMessage());
        return ErrorResponse.builder()
                .error(List.of("[Domain Error] 권한 없음"))
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode(ex.getErrorCode().toString())
                .message(ex.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 낙관적 락 예외 처리 (OptimisticLockException)
    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLockException(OptimisticLockException ex) {
        log.error("[Optimistic lock failure] 낙관적 락 예외: {}", ex.getMessage());
        return ErrorResponse.builder()
                .error(List.of("낙관적 락 예외"))
                .status(HttpStatus.CONFLICT.value())
                .errorCode("OPTIMISTIC_LOCK_EXCEPTION" + " (" + ex.getClass().getSimpleName() + ")")
                .message("동시 수정 충돌 발생. 다시 시도해주세요.")
                .path(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(AlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyDeletedException(AlreadyDeletedException ex,
                                                       HttpServletRequest request) {
        log.error("[Domain Error] 이미 삭제된 데이터: {}", ex.getErrorCode().getMessage());
        return ErrorResponse.builder()
                .error(List.of("[Domain Error] 이미 삭제된 데이터"))
                .status(HttpStatus.CONFLICT.value())
                .errorCode(ex.getErrorCode().toString())
                .message(ex.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MatchingLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleMatchingLimitExceededException(MatchingLimitExceededException ex,
                                                              HttpServletRequest request) {
        log.error("[Domain Error] 매칭 허용 수 초과: {}", ex.getErrorCode().getMessage());
        return ErrorResponse.builder()
                .error(List.of("[Domain Error] 매칭 허용 수 초과"))
                .status(HttpStatus.CONFLICT.value())
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
package com.grow.matching_service.matching.application.exception;

import com.grow.matching_service.matching.presentation.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessDeniedException extends RuntimeException {

    private final ErrorCode errorCode;

    public AccessDeniedException(Throwable cause,
                                 ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
}

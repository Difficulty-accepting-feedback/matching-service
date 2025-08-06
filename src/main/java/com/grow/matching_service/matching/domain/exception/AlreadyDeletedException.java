package com.grow.matching_service.matching.domain.exception;

import com.grow.matching_service.matching.presentation.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlreadyDeletedException extends RuntimeException {
    private final ErrorCode errorCode;

    public AlreadyDeletedException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

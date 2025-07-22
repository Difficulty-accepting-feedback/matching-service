package com.grow.matching_service.matching.application.exception;

import com.grow.matching_service.matching.presentation.exception.ErrorCode;

public class MatchingNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public MatchingNotFoundException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

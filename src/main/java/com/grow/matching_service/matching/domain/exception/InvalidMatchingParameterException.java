package com.grow.matching_service.matching.domain.exception;

import com.grow.matching_service.matching.presentation.exception.ErrorCode;

public class InvalidMatchingParameterException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidMatchingParameterException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
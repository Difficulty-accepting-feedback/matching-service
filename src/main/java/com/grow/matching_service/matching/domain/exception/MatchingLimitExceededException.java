package com.grow.matching_service.matching.domain.exception;

import com.grow.matching_service.matching.presentation.exception.ErrorCode;

public class MatchingLimitExceededException extends RuntimeException {

    private final ErrorCode errorCode;

    public MatchingLimitExceededException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public MatchingLimitExceededException(Throwable cause,
                                          ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

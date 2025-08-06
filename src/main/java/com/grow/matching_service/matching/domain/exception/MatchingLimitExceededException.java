package com.grow.matching_service.matching.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingLimitExceededException extends RuntimeException {

    private final ErrorCode errorCode;
}

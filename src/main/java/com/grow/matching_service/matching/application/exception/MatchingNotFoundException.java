package com.grow.matching_service.matching.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;
}

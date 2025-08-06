package com.grow.matching_service.matching.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MATCHING_NOT_FOUND("404", "매칭 정보가 없습니다." ),;

    private final String code;
    private final String message;
}

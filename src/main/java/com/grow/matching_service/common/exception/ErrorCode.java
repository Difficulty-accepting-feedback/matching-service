package com.grow.matching_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MATCHING_TOO_MANY("400", "카테고리별 3개의 매칭을 초과하였습니다.");

    private final String code;
    private final String message;
}

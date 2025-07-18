package com.grow.matching_service.matching.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_MATCHING_ID("400", "유효하지 않은 매칭 ID 값입니다."),
    INVALID_CATEGORY_ID("400", "유효하지 않은 카테고리 값입니다." ),
    INVALID_MOST_ACTIVE_TIME_ID("400", "유효하지 않은 활동 시간 값입니다."),
    INVALID_LEVEL_ID("400", "유효하지 않은 레벨 값입니다."),
    INVALID_AGE_ID("400", "유효하지 않은 나이 값입니다."),
    INVALID_INTRODUCTION("400", "유효하지 않은 소개글 값입니다."),
    INVALID_MEMBER_ID("400", "유효하지 않은 회원 ID 값입니다." ),
    ;

    private final String code;
    private final String message;
}

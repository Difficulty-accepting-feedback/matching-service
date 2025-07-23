package com.grow.matching_service.matching.domain.enums;

public enum Age {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    FIFTIES("50대"),
    SIXTIES("60대 이상"),
    NONE("선택 없음"); // 기본 값

    private final String description;

    Age(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

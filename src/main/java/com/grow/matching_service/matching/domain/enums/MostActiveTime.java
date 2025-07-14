package com.grow.matching_service.matching.domain.enums;

// 자주 접속하는 시간을 Enum 타입으로 생성
public enum MostActiveTime {
    MORNING("아침 06:00 ~ 12:00"),
    AFTERNOON("오후 12:00 ~ 18:00"),
    EVENING("저녁 18:00 ~ 00:00"),
    DAWN("새벽 00:00 ~ 06:00");

    private final String description;

    MostActiveTime(String description) {
        this.description = description;
    }
}

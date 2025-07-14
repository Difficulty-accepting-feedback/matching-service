package com.grow.matching_service.matching.domain.enums;

public enum Level {
    SEED(1, "씨앗", "완전히 초보자이며, 개념을 접하는 단계"),
    SEEDLING(2, "새싹", "기초 지식을 쌓고 간단한 과제를 수행할 수 있는 단계"),
    SAPLING(3, "나무", "일상적 활용이 가능하며, 문제 해결 경험을 쌓는 단계"),
    BLOOMING(4, "꽃", "다양한 상황에서 능숙하게 적용하고 응용할 수 있는 단계"),
    FRUITFUL(5, "열매", "전문가 수준으로 심화 내용까지 다룰 수 있는 단계");

    private final int level;
    private final String label;
    private final String description;

    Level(int level, String label, String description) {
        this.level = level;
        this.label = label;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}

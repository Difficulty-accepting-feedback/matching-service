package com.grow.matching_service.matching.domain.model;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import com.grow.matching_service.matching.domain.exception.InvalidMatchingParameterException;
import lombok.Getter;

import com.grow.matching_service.matching.domain.enums.Category;

import static com.grow.matching_service.matching.presentation.exception.ErrorCode.*;
import static com.grow.matching_service.matching.presentation.exception.ErrorCode.INVALID_MATCHING_ID;

@Getter
public class Matching {
    private final Long matchingId;
    private final Long memberId;
    private Category category;
    private Age age;
    private MostActiveTime mostActiveTime;
    private Level level;
    private Boolean isAttending;
    private String introduction;

    /** 신규 매칭 생성용 팩토리 */
    public static Matching createNew(Long memberId,
                                     Category category,
                                     MostActiveTime mostActiveTime,
                                     Level level,
                                     Age age,
                                     Boolean isAttending,
                                     String introduction
    ) {
        // 도메인 생성 시 검증 필수
        validateIntroduction(introduction);
        validateRequiredFields(memberId, category, mostActiveTime, level, age); // 공통

        return new Matching(
                null,
                memberId,
                category,
                mostActiveTime,
                level,
                age,
                isAttending,
                introduction
        );
    }

    /** DB 조회 (기존 매칭)용 팩토리 */
    public static Matching loadExisting(Long matchingId,
                                        Long memberId,
                                        Category category,
                                        MostActiveTime mostActiveTime,
                                        Level level,
                                        Age age,
                                        Boolean isAttending,
                                        String introduction
    ) {
        // 로드 시 기본 검증 (DB 데이터를 100% 신뢰하지 않는 것이 좋음)
        validateMatchingId(matchingId); // ID 유효성 (제대로 생성이 됐는지)
        validateRequiredFields(memberId, category, mostActiveTime, level, age);

        return new Matching(
                matchingId,
                memberId,
                category,
                mostActiveTime,
                level,
                age,
                isAttending,
                introduction
        );
    }

    // private 생성자 통합
    private Matching(Long matchingId,
                     Long memberId,
                     Category category,
                     MostActiveTime mostActiveTime,
                     Level level,
                     Age age,
                     Boolean isAttending,
                     String introduction
    ) {
        this.matchingId      = matchingId;
        this.memberId        = memberId;
        this.category        = category;
        this.mostActiveTime  = mostActiveTime;
        this.level           = level;
        this.age             = age;
        this.isAttending     = isAttending;
        this.introduction    = introduction;
    }

    // 공통 검증: 필수 필드 null 체크
    private static void validateRequiredFields(Long memberId,
                                               Category category,
                                               MostActiveTime mostActiveTime,
                                               Level level,
                                               Age age
    ) {
        checkMemberIdField(memberId);
        checkCategoryField(category);
        checkActiveTimeField(mostActiveTime);
        checkLevelField(level);
        checkAgeField(age);
    }

    // 생성 특유 검증 예시: introduction 규칙
    private static void validateIntroduction(String introduction) {
        if (introduction == null || introduction.isBlank() || introduction.length() > 1000) {
            throw new InvalidMatchingParameterException(INVALID_INTRODUCTION);
        }
    }

    // 로드 특유 검증: matchingId 유효성
    private static void validateMatchingId(Long matchingId) {
        if (matchingId == null || matchingId <= 0) {
            throw new InvalidMatchingParameterException(INVALID_MATCHING_ID);
        }
    }

    public void updateCategory(Category newCategory) {
        checkCategoryField(newCategory);
        this.category = newCategory;
    }

    public void updateMostActiveTime(MostActiveTime newMostActiveTime) {
        checkActiveTimeField(newMostActiveTime);
        this.mostActiveTime = newMostActiveTime;
    }

    public void updateLevel(Level newLevel) {
        checkLevelField(newLevel);
        this.level = newLevel;
    }

    public void updateAge(Age newAge) {
        checkAgeField(newAge);
        this.age = newAge;
    }

    public void updateAttendance(boolean attending) {
        this.isAttending = attending;
    }

    public void updateIntroduction(String newIntro) {
        // 공백 체크 및 글자수 제한 추가
        validateIntroduction(newIntro);
        this.introduction = newIntro;
    }

    private static void checkAgeField(Age age) {
        if (age == null) {
            throw new InvalidMatchingParameterException(INVALID_AGE_ID);
        }
    }

    private static void checkLevelField(Level level) {
        if (level == null) {
            throw new InvalidMatchingParameterException(INVALID_LEVEL_ID);
        }
    }

    private static void checkMemberIdField(Long memberId) {
        if (memberId == null || memberId <= 0L) {
            throw new InvalidMatchingParameterException(INVALID_MEMBER_ID);
        }
    }

    private static void checkActiveTimeField(MostActiveTime mostActiveTime) {
        if (mostActiveTime == null) {
            throw new InvalidMatchingParameterException(INVALID_MOST_ACTIVE_TIME_ID);
        }
    }

    private static void checkCategoryField(Category category) {
        if (category == null) {
            throw new InvalidMatchingParameterException(INVALID_CATEGORY_ID);
        }
    }

}

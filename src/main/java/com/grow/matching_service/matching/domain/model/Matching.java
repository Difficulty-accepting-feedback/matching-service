package com.grow.matching_service.matching.domain.model;

import com.grow.matching_service.matching.domain.exception.*;
import com.grow.matching_service.matching.domain.enums.*;
import lombok.Getter;

import java.util.List;

import static com.grow.matching_service.matching.domain.exception.ErrorCode.*;

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
    private MatchingStatus status;
    private Long version;

    /** 신규 매칭 생성용 팩토리 */
    public static Matching createNew(Long memberId,
                                     Category category,
                                     MostActiveTime mostActiveTime,
                                     Level level,
                                     Age age,
                                     Boolean isAttending,
                                     String introduction,
                                     List<Matching> existingMatchings
    ) {
        // 도메인 생성 시 검증 필수
        validateIntroduction(introduction);
        validateRequiredFields(memberId, category, mostActiveTime, level, age); // 공통
        if (existingMatchings.size() >= 3) { // 최대 매칭 수 제한
            throw new MatchingLimitExceededException(ErrorCode.MATCHING_LIMIT_EXCEEDED);
        }

        return new Matching(
                null,
                memberId,
                category,
                mostActiveTime,
                level,
                age,
                isAttending,
                introduction,
                null, // 기본 버전은 0
                MatchingStatus.ACTIVE // 기본 상태는 ACTIVE
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
                                        String introduction,
                                        Long version,
                                        MatchingStatus status
    ) {
        // 로드 시 기본 검증 (DB 데이터를 100% 신뢰하지 않는 것이 좋음)
        validateMatchingId(matchingId); // ID 유효성 (제대로 생성이 됐는지)
        validateRequiredFields(memberId, category, mostActiveTime, level, age);
        checkStatusField(status);

        return new Matching(
                matchingId,
                memberId,
                category,
                mostActiveTime,
                level,
                age,
                isAttending,
                introduction,
                version,
                status
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
                     String introduction,
                     Long version,
                     MatchingStatus status
    ) {
        this.matchingId      = matchingId;
        this.memberId        = memberId;
        this.category        = category;
        this.mostActiveTime  = mostActiveTime;
        this.level           = level;
        this.age             = age;
        this.isAttending     = isAttending;
        this.introduction    = introduction;
        this.version         = version;
        this.status          = status;
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

    // ==== 업데이트 로직 ==== //
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

    public void updateStatus(MatchingStatus status) {
        this.status = status;
    }

    // ==== 유효성 검증 ==== //
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

    private static void checkStatusField(MatchingStatus status) {
        if (status == null) {
            throw new InvalidMatchingParameterException(INVALID_MATCHING_STATUS_ID);
        }
    }

    public void delete(Long memberId) {
        // 해당 매칭이 본인 것인지 확인하기 -> 아니면 예외 처리
        if (!this.memberId.equals(memberId)) {
            throw new AccessDeniedException(ErrorCode.MATCHING_OWNERSHIP_MISMATCH);
        }

        // 해당 매칭이 삭제된 상태인지 확인하기 -> 아니면 예외 처리
        if (this.status.equals(MatchingStatus.DELETED)) {
            throw new AlreadyDeletedException(ErrorCode.MATCHING_ALREADY_DELETED);
        }

        this.status = MatchingStatus.DELETED; // 삭제 상태로 변경 soft delete
    }
}

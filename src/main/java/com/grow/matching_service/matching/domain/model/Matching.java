package com.grow.matching_service.matching.domain.model;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import lombok.Getter;

import com.grow.matching_service.matching.domain.enums.Category;

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

    public void updateCategory(Category newCategory) {
        this.category = newCategory;
    }

    public void updateMostActiveTime(MostActiveTime newMostActiveTime) {
        this.mostActiveTime = newMostActiveTime;
    }

    public void updateLevel(Level newLevel) {
        this.level = newLevel;
    }

    public void updateAge(Age newAge) {
        this.age = newAge;
    }

    public void updateAttendance(boolean attending) {
        this.isAttending = attending;
    }

    public void updateIntroduction(String newIntro) {
        this.introduction = newIntro;
    }
}

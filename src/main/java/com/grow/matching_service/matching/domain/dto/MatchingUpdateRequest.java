package com.grow.matching_service.matching.domain.dto;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import lombok.Getter;

/**
 * 필드 업데이트를 위한 request DTO 클래스 (null 허용)
 */
@Getter
public class MatchingUpdateRequest {
    private MostActiveTime mostActiveTime;
    private Level level;
    private Age age;
    private Boolean isAttending;
    private String introduction;

    public MatchingUpdateRequest(MostActiveTime mostActiveTime,
                                 Level level,
                                 Age age,
                                 Boolean isAttending,
                                 String introduction
    ) {
        this.mostActiveTime = mostActiveTime;
        this.level = level;
        this.age = age;
        this.isAttending = isAttending;
        this.introduction = introduction;
    }
}

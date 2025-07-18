package com.grow.matching_service.matching.application.dto;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import com.grow.matching_service.matching.domain.model.Matching;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponse {

    private Long matchingId;
    private Category category;
    private MostActiveTime mostActiveTime;
    private Level level;
    private Age age;
    private Boolean isAttending;
    private String introduction;

    // 도메인 객체를 DTO로 변환하는 정적 팩토리 메서드
    public static MatchingResponse from(Matching matching) {
        return MatchingResponse.builder()
                .matchingId(matching.getMatchingId())
                .category(matching.getCategory())
                .mostActiveTime(matching.getMostActiveTime())
                .level(matching.getLevel())
                .age(matching.getAge())
                .isAttending(matching.getIsAttending())
                .introduction(matching.getIntroduction())
                .build();
    }
}

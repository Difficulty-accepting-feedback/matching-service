package com.grow.matching_service.matching.infra.dto;

import com.grow.matching_service.matching.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingResult {
    private Long memberId;
    private Category category;
    private MostActiveTime mostActiveTime;
    private Level level;
    private Age age;
    private Boolean isAttending;
    private String introduction;
    private MatchingStatus status;
    private Integer score;
}
package com.grow.matching_service.matching.infra.dto;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 이벤트 발행 후 DTO 객체로 변경한 후 쿼리를 실행하기 위한 DTO 클래스
 */
@Getter
@Builder
public class MatchingQueryDto {
    private Long memberId;
    private Category category;
    private MostActiveTime mostActiveTime;
    private Level level;
    private Age age;
    private Boolean isAttending;
}

package com.grow.matching_service.matching.presentation.dto;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;
    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;
    @NotNull(message = "활동 시간은 필수입니다.")
    private MostActiveTime mostActiveTime;
    @NotNull(message = "레벨은 필수입니다.")
    private Level level;
    @NotNull(message = "나이는 필수입니다.")
    private Age age;
    @NotNull(message = "오프라인 모임 참석 여부는 필수입니다.")
    private Boolean isAttending;
    @NotBlank(message = "소개글은 필수입니다.")
    @Size(min = 10, max = 500, message = "소개글은 10자 이상 500자 이하로 작성해주세요.")
    private String introduction;
}
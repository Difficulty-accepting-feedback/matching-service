package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.application.dto.MatchingResponse;
import com.grow.matching_service.matching.domain.dto.MatchingUpdateRequest;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;

import java.util.List;

public interface MatchingService {
    void createMatching(MatchingRequest request, Long memberId);
    List<MatchingResponse> getMatchingsByCategory(Category category, Long memberId);
    void updateMatching(Long matchingId, MatchingUpdateRequest request);
    void deleteMatching(Long matchingId, Long memberId);
}

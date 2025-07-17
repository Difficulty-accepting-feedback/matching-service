package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.presentation.dto.MatchingRequest;

public interface MatchingService {
    void createMatching(MatchingRequest request);
}

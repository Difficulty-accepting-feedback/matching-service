package com.grow.matching_service.matching.domain.model;

import com.grow.matching_service.matching.domain.enums.*;
import com.grow.matching_service.matching.domain.exception.InvalidMatchingParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 도메인에서 제대로 예외를 던지고 있는지 확인하기 위한 테스트 로직 작성
 */
class MatchingTest {

    @Test
    @DisplayName("createNew 성공 케이스: 모든 필드 유효")
    void testCreateNew_ValidInputs() {
        Matching matching = Matching.createNew(
                1L,
                Category.STUDY,
                MostActiveTime.MORNING,
                Level.SEED,
                Age.TEENS,
                true,
                "유효한 소개글",
                List.of()
        );
        assertNotNull(matching);
        assertNull(matching.getMatchingId()); // 신규이니 ID null
        assertEquals(1L, matching.getMemberId());
    }

    @Test
    @DisplayName("createNew 실패 케이스: 필수 필드 null")
    void testCreateNew_InvalidRequiredFields() {
        // memberId null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(null, Category.STUDY, MostActiveTime.MORNING, Level.SEED, Age.TEENS, true, "소개", List.of()));

        // category null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, null, MostActiveTime.MORNING, Level.SEED, Age.TEENS, true, "소개", List.of()));

        // mostActiveTime null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, Category.STUDY, null, Level.SEED, Age.TEENS, true, "소개", List.of()));

        // level null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, Category.STUDY, MostActiveTime.MORNING, null, Age.TEENS, true, "소개", List.of()));

        // age null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, Category.STUDY, MostActiveTime.MORNING, Level.SEED, null, true, "소개", List.of()));
    }

    @Test
    @DisplayName("createNew 실패 케이스: introduction 규칙 위반 (길이 초과 또는 blank)")
    void testCreateNew_InvalidIntroduction() {
        // 길이 초과 (1000자 초과)
        String longIntro = "a".repeat(1001);
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, Category.STUDY, MostActiveTime.MORNING, Level.SEED, Age.TEENS, true, longIntro, List.of()));

        // blank
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.createNew(1L, Category.STUDY, MostActiveTime.MORNING, Level.SEED, Age.TEENS, true, " ", List.of()));
    }

    @Test
    @DisplayName("loadExisting 성공 케이스: 모든 필드 유효")
    void testLoadExisting_ValidInputs() {
        Matching matching = Matching.loadExisting(
                1L,
                1L,
                Category.HOBBY,
                MostActiveTime.AFTERNOON,
                Level.BLOOMING,
                Age.TWENTIES,
                false,
                "유효한 소개글",
                1L,
                MatchingStatus.ACTIVE
        );
        assertNotNull(matching);
        assertEquals(1L, matching.getMatchingId());
        assertEquals(1L, matching.getMemberId());
    }

    @Test
    @DisplayName("loadExisting 실패 케이스: matchingId 무효 (null 이거나 0L)")
    void testLoadExisting_InvalidMatchingId() {
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.loadExisting(null, 1L, Category.HOBBY, MostActiveTime.AFTERNOON, Level.BLOOMING, Age.TWENTIES, false, "소개", 1L, MatchingStatus.ACTIVE));

        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.loadExisting(0L, 1L, Category.HOBBY, MostActiveTime.AFTERNOON, Level.BLOOMING, Age.TWENTIES, false, "소개", 1L, MatchingStatus.ACTIVE));
    }

    @Test
    @DisplayName("loadExisting 실패 케이스: 필수 필드 null")
    void testLoadExisting_InvalidRequiredFields() {
        // memberId null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.loadExisting(1L, null, Category.HOBBY, MostActiveTime.AFTERNOON, Level.BLOOMING, Age.TWENTIES, false, "소개", 1L, MatchingStatus.ACTIVE));

        // category null
        assertThrows(InvalidMatchingParameterException.class, () ->
                Matching.loadExisting(1L, 1L, null, MostActiveTime.AFTERNOON, Level.BLOOMING, Age.TWENTIES, false, "소개", 1L, MatchingStatus.ACTIVE));
    }
}
package com.grow.matching_service.common.exception.domain;

import com.grow.matching_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
}

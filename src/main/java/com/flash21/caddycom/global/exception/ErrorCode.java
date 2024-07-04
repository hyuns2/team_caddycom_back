package com.flash21.caddycom.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 대문자로 ErrorCode명 정의 (상태코드 / HttpStatus / 메세지)
     */
    USER_NOT_FOUND("사용자를 찾을 수 없습니다");

    private final String message;
}

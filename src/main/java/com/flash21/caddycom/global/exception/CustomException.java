package com.flash21.caddycom.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}

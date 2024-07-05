package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CInvalidTimeOrderException extends RuntimeException {
    ErrorCode errorCode;

    public CInvalidTimeOrderException() {
        super();
        this.errorCode = ErrorCode.INVALID_TIME_ORDER;
    }
}

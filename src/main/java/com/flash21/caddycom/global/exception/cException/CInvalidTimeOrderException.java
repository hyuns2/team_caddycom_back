package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;

public class CInvalidTimeOrderException {
    ErrorCode errorCode;

    public CInvalidTimeOrderException() {
        super();
        this.errorCode = ErrorCode.INVALID_TIME_ORDER;
    }
}

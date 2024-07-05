package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;

public class CInvalidDateOrderException {
    ErrorCode errorCode;

    public CInvalidDateOrderException() {
        super();
        this.errorCode = ErrorCode.INVALID_DATE_ORDER;
    }
}

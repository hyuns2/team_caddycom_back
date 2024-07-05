package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CInvalidDateOrderException extends RuntimeException {
    ErrorCode errorCode;

    public CInvalidDateOrderException() {
        super();
        this.errorCode = ErrorCode.INVALID_DATE_ORDER;
    }
}

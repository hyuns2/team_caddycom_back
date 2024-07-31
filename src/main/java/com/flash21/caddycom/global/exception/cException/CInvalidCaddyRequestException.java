package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CInvalidCaddyRequestException extends RuntimeException {
    ErrorCode errorCode;

    public CInvalidCaddyRequestException() {
        super();
        this.errorCode = ErrorCode.INVALID_CADDY_REQUEST;
    }
}

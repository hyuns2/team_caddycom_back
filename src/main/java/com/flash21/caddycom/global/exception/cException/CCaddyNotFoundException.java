package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CCaddyNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CCaddyNotFoundException() {
        super();
        this.errorCode = ErrorCode.CADDY_NOT_FOUND;
    }
}

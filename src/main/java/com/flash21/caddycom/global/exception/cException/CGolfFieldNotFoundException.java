package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CGolfFieldNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CGolfFieldNotFoundException() {
        super();
        this.errorCode = ErrorCode.GOLF_FIELD_NOT_FOUND;
    }
}

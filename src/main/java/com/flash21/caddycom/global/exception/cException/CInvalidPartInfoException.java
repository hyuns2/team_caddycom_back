package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CInvalidPartInfoException extends RuntimeException {
    ErrorCode errorCode;

    public CInvalidPartInfoException() {
        super();
        this.errorCode = ErrorCode.INVALID_PART_INFO;
    }
}

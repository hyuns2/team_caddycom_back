package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CInvalidModifyingRequestException extends RuntimeException {
    ErrorCode errorCode;

    public CInvalidModifyingRequestException() {
        super();
        this.errorCode = ErrorCode.INVALID_MODIFYING_REQUEST;
    }
}

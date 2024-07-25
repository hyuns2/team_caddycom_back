package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CBadReservationRequestException extends RuntimeException {
    ErrorCode errorCode;

    public CBadReservationRequestException() {
        super();
        this.errorCode = ErrorCode.BAD_RESERVATION_REQUEST;
    }
}

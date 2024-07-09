package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CReservationDateNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CReservationDateNotFoundException() {
        super();
        this.errorCode = ErrorCode.RESERVATION_DATE_NOT_FOUND;
    }
}

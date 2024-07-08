package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CReservationSheetNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CReservationSheetNotFoundException() {
        super();
        this.errorCode = ErrorCode.RESERVATION_SHEET_NOT_FOUND;
    }
}

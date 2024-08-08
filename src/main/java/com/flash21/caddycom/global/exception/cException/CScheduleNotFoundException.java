package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CScheduleNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CScheduleNotFoundException() {
        super();
        this.errorCode = ErrorCode.SCHEDULE_NOT_FOUND;
    }
}

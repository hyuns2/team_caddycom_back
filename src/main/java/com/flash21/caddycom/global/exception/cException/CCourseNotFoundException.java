package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CCourseNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CCourseNotFoundException() {
        super();
        this.errorCode = ErrorCode.COURSE_NOT_FOUND;
    }
}

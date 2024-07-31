package com.flash21.caddycom.global.exception.cException;

import com.flash21.caddycom.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CTeamNameNotFoundException extends RuntimeException {
    ErrorCode errorCode;

    public CTeamNameNotFoundException() {
        super();
        this.errorCode = ErrorCode.TEAM_NAME_NOT_FOUND;
    }
}

package com.flash21.caddycom.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Getter
public class ExceptionDto {
    private HttpStatus httpStatus;
    private int code;
    private String message;

    public static ExceptionDto fail(HttpStatus status, String message){
        return ExceptionDto.builder()
                .httpStatus(status)
                .code(status.value())
                .message(message)
                .build();
    }

    public static ExceptionDto fail(ErrorCode errorCode){
        return ExceptionDto.builder()
                .httpStatus(errorCode.getHttpStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}

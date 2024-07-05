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

    public static ExceptionDto fail(HttpStatus code, ErrorCode errorCode){
        return ExceptionDto.builder()
                .httpStatus(code)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
    public static ExceptionDto fail(HttpStatus code, String message){
        return ExceptionDto.builder()
                .httpStatus(code)
                .code(code.value())
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

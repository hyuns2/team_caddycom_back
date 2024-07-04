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
    private String message;

    public static ExceptionDto fail(HttpStatus code, ErrorCode errorCode){
        return ExceptionDto.builder()
                .httpStatus(code)
                .message(errorCode.getMessage())
                .build();
    }
    public static ExceptionDto fail(HttpStatus code, String message){
        return ExceptionDto.builder()
                .httpStatus(code)
                .message(message)
                .build();
    }
}

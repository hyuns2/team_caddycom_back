package com.flash21.caddycom.global.exception;

import com.flash21.caddycom.global.exception.cException.CInvalidCourseException;
import com.flash21.caddycom.global.exception.cException.CInvalidDateOrderException;
import com.flash21.caddycom.global.exception.cException.CInvalidPartInfoException;
import com.flash21.caddycom.global.exception.cException.CInvalidTimeOrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> illegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST,e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDto> noSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionDto.fail(HttpStatus.NOT_FOUND,ErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(CInvalidPartInfoException.class)
    protected ResponseEntity<ExceptionDto> handle(CInvalidPartInfoException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CInvalidDateOrderException.class)
    protected ResponseEntity<ExceptionDto> handle(CInvalidDateOrderException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CInvalidTimeOrderException.class)
    protected ResponseEntity<ExceptionDto> handle(CInvalidTimeOrderException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CInvalidCourseException.class)
    protected ResponseEntity<ExceptionDto> handle(CInvalidCourseException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }
}

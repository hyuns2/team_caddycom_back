package com.flash21.caddycom.global.exception;

import com.flash21.caddycom.global.exception.cException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> illegalStateException(IllegalStateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST,e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> illegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST,e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDto> noSuchElementException(NoSuchElementException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionDto.fail(HttpStatus.NOT_FOUND,e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        log.error(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST, "요청 데이터가 잘못되었습니다. 누락되거나, 올바른 타입인지 확인하세요."));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDto> sqlException(SQLException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDto> indexOutOfBoundsException(IndexOutOfBoundsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDto> nullPointerException(NullPointerException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.fail(HttpStatus.BAD_REQUEST, "입력값이 잘못되었습니다.: " + e.getMessage()));
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

    @ExceptionHandler(CCourseNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CCourseNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CReservationSheetNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CReservationSheetNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CReservationDateNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CReservationDateNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CGolfFieldNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CGolfFieldNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CBadReservationRequestException.class)
    protected ResponseEntity<ExceptionDto> handle(CBadReservationRequestException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CTeamNameNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CTeamNameNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CCaddyNotFoundException.class)
    protected ResponseEntity<ExceptionDto> handle(CCaddyNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }

    @ExceptionHandler(CInvalidCaddyRequestException.class)
    protected ResponseEntity<ExceptionDto> handle(CInvalidCaddyRequestException e) {
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ExceptionDto.fail(errorCode));
    }
}

package com.flash21.caddycom.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 대문자로 ErrorCode명 정의 (상태코드 / HttpStatus / 메세지)
     */
    // UserService
    USER_NOT_FOUND(600, "사용자를 찾을 수 없습니다", HttpStatus.BAD_REQUEST),

    // CourseService
    COURSE_NOT_FOUND(700, "해당하는 코스를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // ReservationSheetService
    INVALID_PART_INFO(810, "부(파트)에 대한 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_DATE_ORDER(820, "날짜의 순서가 맞지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TIME_ORDER(830, "시간의 순서가 맞지 않습니다.", HttpStatus.BAD_REQUEST),

    // AssignmentService
    RESERVATION_SHEET_NOT_FOUND(840, "해당하는 예약 시트를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_DATE_NOT_FOUND(850, "해당하는 예약 시트의 날짜를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

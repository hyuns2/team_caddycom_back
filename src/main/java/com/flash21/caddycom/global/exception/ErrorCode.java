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
    GOLF_FIELD_NOT_FOUND(601, "골프장을 찾을 수 없습니다", HttpStatus.BAD_REQUEST),

    // CourseService
    COURSE_NOT_FOUND(700, "해당하는 코스를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // ReservationSheetService
    BAD_RESERVATION_REQUEST(805, "일부 코스와 날짜에 대한 예약이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PART_INFO(810, "부(파트)에 대한 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_DATE_ORDER(820, "날짜의 순서가 맞지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TIME_ORDER(830, "시간의 순서가 맞지 않습니다.", HttpStatus.BAD_REQUEST),

    // AssignmentService
    RESERVATION_SHEET_NOT_FOUND(840, "해당하는 예약 시트를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_FOUND(850, "해당하는 예약 시트의 스케줄을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_MODIFYING_REQUEST(860, "블락 또는 배정된 시간이 존재하여 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // HouseCaddyService
    CADDY_NOT_FOUND(910, "해당하는 캐디를 찾을 수 없습니다.",HttpStatus.BAD_REQUEST),
    TEAM_NAME_NOT_FOUND(920, "해당하는 팀을 찾을 수 없습니다.",HttpStatus.BAD_REQUEST),
    INVALID_CADDY_REQUEST(930, "휴무일 변경을 요청하지 않은 캐디입니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

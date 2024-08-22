package com.flash21.caddycom.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssignmentStatus {
    NOTHING(0),             //아무것도 없는 초기 상태
    BLOCKED(1),             //블락된 상태
    CANCELED(2),            //취소된 상태
    ASSIGNED(3),            //배정된 상태
    CANCEL_REQUESTED(4),    //취소 요청된 상태
    DELETED(5),             //삭제된 상태
    ASSIGN_REQUESTED(6);    //프리캐디에 배정 요청된 상태

    private final int number;
}

package com.flash21.caddycom.controller.notification;

import com.flash21.caddycom.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * 알림 기능은 미완성기능으로 구현이 필요합니다.
 * 아래 API는 SSE를 이용한 알림기능의 테스트를 위한 API입니다.
 */
@Tag(name = "7. Notification", description = "알림 기능 API")
@RestController
@RequestMapping("api/notification")
@RequiredArgsConstructor
@Hidden
public class NotificationController {
    private final NotificationService notificationService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/assignment/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "캐디 배정알림 API", description="예약이 배정되었을 경우 해당 캐디에게 알림을 보낸다.")
    public SseEmitter notifyAssignment(@PathVariable Long id) {
        return notificationService.subscribe(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/unsubscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "알림 구독취소 API", description="알림 구독을 취소한다.")
    public void unsubscribe(@PathVariable Long id) {
        notificationService.unsubscribe(id);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/publish/{id}")
    public void test(@PathVariable Long id) {
        notificationService.publish(id, "알림이 전송되었습니다.");
    }
}

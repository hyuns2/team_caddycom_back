package com.flash21.caddycom.service.notification;

import com.flash21.caddycom.repository.notification.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long id) {
        SseEmitter emitter = createEmitter(id);

        sendToClient(id, "알림이 구독되었습니다.");
        return emitter;
    }

    public void publish(Long id, Object data) {
        sendToClient(id, data);
    }

    private void sendToClient(Long id, Object data) {
        SseEmitter emitter = emitterRepository.findById(id);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data(data));
            } catch (Exception e) {
                emitterRepository.deleteById(id);
                emitter.completeWithError(e);
            }
        }
    }

    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));
        emitter.onError((e) -> emitterRepository.deleteById(id));

        return emitter;
    }
}

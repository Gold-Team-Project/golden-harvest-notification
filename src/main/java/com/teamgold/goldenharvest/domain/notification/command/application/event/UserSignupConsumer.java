package com.teamgold.goldenharvest.domain.notification.command.application.event;

import com.teamgold.goldenharvest.domain.notification.command.application.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSignupConsumer {

    private final NotificationCommandService notificationCommandService;

    @KafkaListener(topics = "user.signup", groupId = "notification-signup-group")
    public void consumeSignup(UserSignupEvent event) {
        log.info("Consumed signup event: {}", event.email());
        notificationCommandService.createSignupNotificationForAdmin(event);
    }

    public static record UserSignupEvent(String email, String name, String company) {
    }
}

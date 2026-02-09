package com.teamgold.goldenharvest.domain.notification.command.application.service;

import com.teamgold.goldenharvest.domain.notification.command.application.event.SignupPendingEvent;
import com.teamgold.goldenharvest.domain.notification.command.domain.aggregate.UserNotification;
import com.teamgold.goldenharvest.domain.notification.command.domain.repository.UserNotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final UserNotificationRepository userNotificationRepository;
    private final com.teamgold.goldenharvest.domain.notification.command.domain.repository.NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void NotificationRecieved(SignupPendingEvent e) {
        UserNotification userNotification = UserNotification.builder()
                .userEmail(e.getUserEmail()).build();

        userNotificationRepository.save(userNotification);
    }

    @Transactional
    public void createSignupNotificationForAdmin(
            com.teamgold.goldenharvest.domain.notification.command.application.event.UserSignupConsumer.UserSignupEvent event) {
        String templateId = "USER_SIGNUP";
        com.teamgold.goldenharvest.domain.notification.command.domain.aggregate.NotificationTemplate template = notificationRepository
                .findById(templateId)
                .orElseGet(() -> {
                    com.teamgold.goldenharvest.domain.notification.command.domain.aggregate.NotificationTemplate newTemplate = com.teamgold.goldenharvest.domain.notification.command.domain.aggregate.NotificationTemplate
                            .builder()
                            .type(templateId)
                            .title("신규 회원 가입 요청")
                            .body("새로운 회원이 가입을 요청했습니다.")
                            .build();
                    return notificationRepository.save(newTemplate);
                });

        // 어드민 이메일이 명확하지 않아 임시 이메일 사용
        String adminEmail = "admin@goldenharvest.com";

        UserNotification notification = UserNotification.builder()
                .templateType(template)
                .userEmail(adminEmail)
                .isRead(false)
                .build();

        userNotificationRepository.save(notification);
    }

    @Transactional
    public void DeleteAllNotification(String userEmail) {
        userNotificationRepository.deleteAllByUserEmail(userEmail);
    }

    @Transactional
    public void DeleteNotificationById(Long notificationid) {
        userNotificationRepository.deleteByUserNotificationId(notificationid);
    }

    @Transactional
    public void markAsRead(Long userNotificationId) {
        UserNotification un = userNotificationRepository.findByUserNotificationId(userNotificationId)
                .orElseThrow(() -> new EntityNotFoundException("UserNotification not found: " + userNotificationId));

        if (!un.isRead()) { // 이미 읽음이면 굳이 업데이트 안 함(선택)
            un.markAsRead(LocalDateTime.now());
        }
        // save() 안 해도 됨 (영속 상태 + Dirty Checking)
    }

}

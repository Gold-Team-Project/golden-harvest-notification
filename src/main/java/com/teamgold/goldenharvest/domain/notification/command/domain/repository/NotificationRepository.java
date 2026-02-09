package com.teamgold.goldenharvest.domain.notification.command.domain.repository;

import com.teamgold.goldenharvest.domain.notification.command.domain.aggregate.NotificationTemplate;

import java.util.Optional;

public interface NotificationRepository {
    Optional<NotificationTemplate> findById(String type);

    NotificationTemplate save(NotificationTemplate template);
}

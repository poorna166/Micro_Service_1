package com.techcommerce.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techcommerce.notification_service.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    

}

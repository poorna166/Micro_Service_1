package com.techcommerce.notification_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;     // EMAIL, SMS, PUSH
    private String toUser;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;
    private String status;   // SENT, FAILED
}

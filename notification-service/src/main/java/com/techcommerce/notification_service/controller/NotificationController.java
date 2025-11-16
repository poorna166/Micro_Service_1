package com.techcommerce.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techcommerce.notification_service.dto.EmailRequest;
import com.techcommerce.notification_service.dto.SmsRequest;
import com.techcommerce.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Service", description = "Send Email & SMS")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest req) {
        service.sendEmail(req);
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest req) {
        service.sendSms(req);
        return ResponseEntity.ok("SMS sent successfully");
    }
}

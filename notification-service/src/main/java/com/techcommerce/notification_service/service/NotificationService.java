package com.techcommerce.notification_service.service;

import com.techcommerce.notification_service.dto.EmailRequest;
import com.techcommerce.notification_service.dto.SmsRequest;

public interface NotificationService {
    void sendEmail(EmailRequest request);
    void sendSms(SmsRequest request);
}

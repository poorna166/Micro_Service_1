package com.techcommerce.notification_service.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.techcommerce.notification_service.dto.EmailRequest;
import com.techcommerce.notification_service.dto.SmsRequest;
import com.techcommerce.notification_service.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(EmailRequest request) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(request.getTo());
        msg.setSubject(request.getSubject());
        msg.setText(request.getMessage());
        mailSender.send(msg);
    }

    @Override
    public void sendSms(SmsRequest request) {
        // Example only—use Twilio or similar service here
        System.out.println("SMS sent to " + request.getPhoneNumber() + ": " + request.getMessage());
    }
}

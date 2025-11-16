package com.techcommerce.notification_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequest {
    private String phoneNumber;
    private String message;
}

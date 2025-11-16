package com.techcommerce.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String type; // HOME / WORK
    @Builder.Default
    private Boolean isDefault = Boolean.FALSE;
}

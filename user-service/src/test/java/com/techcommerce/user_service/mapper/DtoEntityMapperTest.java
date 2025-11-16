package com.techcommerce.user_service.mapper;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoEntityMapperTest {

    @Test
    void mapNewUser_and_applyUpdate() {
        AddressDto a = AddressDto.builder()
                .fullName("John Addr")
                .city("City")
                .isDefault(true)
                .build();

        UserDto dto = UserDto.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("secret")
                .addresses(List.of(a))
                .build();

        User u = DtoEntityMapper.mapNewUser(dto);
        assertThat(u.getFullName()).isEqualTo("John Doe");
        assertThat(u.getEmail()).isEqualTo("john@example.com");
        assertThat(u.getAddresses()).hasSize(1);
        assertThat(u.getAddresses().get(0).getFullName()).isEqualTo("John Addr");

        // apply update partial
        UserDto patch = UserDto.builder().fullName("Jane").build();
        DtoEntityMapper.applyUpdateToUser(u, patch);
        assertThat(u.getFullName()).isEqualTo("Jane");
    }
}

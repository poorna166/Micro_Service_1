package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.UserDto;
import com.techcommerce.admin_service.feignclients.UserClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserClient userClient;

    public AdminUserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public UserDto createUser(UserDto dto) {
        return userClient.createUser(dto);
    }

    public UserDto getUser(Long id) {
        return userClient.getUser(id);
    }

    public List<UserDto> listUsers() {
        return userClient.listUsers();
    }

    public UserDto updateUser(Long id, UserDto dto) {
        return userClient.updateUser(id, dto);
    }

    public void deleteUser(Long id) {
        userClient.deleteUser(id);
    }
}

package com.techcommerce.user_service.service;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service port/interface for user-related operations. Implementations should provide transactional
 * behavior and any business rules.
 */
public interface UserServicePort {
    User createUser(UserDto dto);

    Optional<User> getUser(Long id);

    List<User> listUsers();

    User updateUser(Long id, UserDto dto);

    void deleteUser(Long id);

    Address addAddress(Long userId, AddressDto dto);

    List<Address> listAddresses(Long userId);

    Address updateAddress(Long id, AddressDto dto);

    void deleteAddress(Long id);
}

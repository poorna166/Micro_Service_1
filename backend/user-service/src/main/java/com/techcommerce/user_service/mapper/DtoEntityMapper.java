package com.techcommerce.user_service.mapper;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized mapper for mapping between DTOs and Entities.
 * Contains separate methods for create (mapNew...) and update (applyUpdate...)
 */
public final class DtoEntityMapper {

    private DtoEntityMapper() {}

    // -- User mapping
    public static User mapNewUser(UserDto dto) {
        if (dto == null) return null;
        User u = new User();
        u.setFullName(dto.getFullName());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setPassword(dto.getPassword());
        u.setRole(dto.getRole());
        if (dto.getAddresses() != null) {
            List<Address> addrs = dto.getAddresses().stream()
                    .map(DtoEntityMapper::mapNewAddress)
                    .collect(Collectors.toList());
            addrs.forEach(a -> a.setUser(u));
            u.setAddresses(addrs);
        }
        return u;
    }

    public static void applyUpdateToUser(User existing, UserDto dto) {
        if (existing == null || dto == null) return;
        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getPassword() != null) existing.setPassword(dto.getPassword());
        if (dto.getRole() != null) existing.setRole(dto.getRole());

        // Replace addresses if provided
        if (dto.getAddresses() != null) {
            List<Address> addrs = new ArrayList<>();
            for (AddressDto adto : dto.getAddresses()) {
                Address a = (adto.getId() == null) ? mapNewAddress(adto) : mapExistingAddress(adto);
                a.setUser(existing);
                addrs.add(a);
            }
            existing.getAddresses().clear();
            existing.getAddresses().addAll(addrs);
        }
    }

    // -- Address mapping
    public static Address mapNewAddress(AddressDto dto) {
        if (dto == null) return null;
        Address a = new Address();
        a.setFullName(dto.getFullName());
        a.setPhone(dto.getPhone());
        a.setStreet(dto.getStreet());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setPincode(dto.getPincode());
        a.setType(dto.getType());
        a.setIsDefault(dto.getIsDefault());
        return a;
    }

    public static Address mapExistingAddress(AddressDto dto) {
        Address a = mapNewAddress(dto);
        a.setId(dto.getId());
        return a;
    }
}

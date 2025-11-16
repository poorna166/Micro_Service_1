package com.techcommerce.user_service.repository.dao;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.mapper.DtoEntityMapper;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;
import com.techcommerce.user_service.repository.AddressRepository;
import com.techcommerce.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class AddressDao {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressDao(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Address create(AddressDto dto) {
        if (dto.getUserId() == null) throw new IllegalArgumentException("userId required for address create");
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.getUserId()));
        Address a = DtoEntityMapper.mapNewAddress(dto);
        a.setUser(user);
        return addressRepository.save(a);
    }

    @Transactional
    public Address update(Long id, AddressDto dto) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found: " + id));
        // apply fields from dto selectively
        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getStreet() != null) existing.setStreet(dto.getStreet());
        if (dto.getCity() != null) existing.setCity(dto.getCity());
        if (dto.getState() != null) existing.setState(dto.getState());
        if (dto.getPincode() != null) existing.setPincode(dto.getPincode());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getIsDefault() != null) existing.setIsDefault(dto.getIsDefault());

        // optionally change user association
        if (dto.getUserId() != null && !dto.getUserId().equals(existing.getUser().getId())) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.getUserId()));
            existing.setUser(user);
        }

        return addressRepository.save(existing);
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public java.util.List<Address> findByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}

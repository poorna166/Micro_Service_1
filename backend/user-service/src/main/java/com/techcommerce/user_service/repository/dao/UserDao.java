package com.techcommerce.user_service.repository.dao;

import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.mapper.DtoEntityMapper;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;
import com.techcommerce.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserDao {

    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String plain) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public User create(UserDto dto) {
        User u = DtoEntityMapper.mapNewUser(dto);
        // hash password if provided
        if (dto.getPassword() != null) {
            u.setPassword(hashPassword(dto.getPassword()));
        }
        // addresses already set with user reference in mapper
        return userRepository.save(u);
    }

    @Transactional
    public User update(Long id, UserDto dto) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) throw new EntityNotFoundException("User not found: " + id);
        User existing = opt.get();
        DtoEntityMapper.applyUpdateToUser(existing, dto);
        // if password provided in dto, hash and set
        if (dto.getPassword() != null) {
            existing.setPassword(hashPassword(dto.getPassword()));
        }
        // ensure addresses have back-reference
        for (Address a : existing.getAddresses()) a.setUser(existing);
        return userRepository.save(existing);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}

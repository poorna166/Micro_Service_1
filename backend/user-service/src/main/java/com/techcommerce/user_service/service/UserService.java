package com.techcommerce.user_service.service;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;
import com.techcommerce.user_service.repository.dao.AddressDao;
import com.techcommerce.user_service.repository.dao.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServicePort {

    private final UserDao userDao;
    private final AddressDao addressDao;

    public UserService(UserDao userDao, AddressDao addressDao) {
        this.userDao = userDao;
        this.addressDao = addressDao;
    }

    // We now delegate create/update to DAOs which accept DTOs. Keep previous logic (password hashing, default handling)
    public User createUser(UserDto dto) {
        return userDao.create(dto);
    }

    public Optional<User> getUser(Long id) { return userDao.findById(id); }

    public List<User> listUsers() { return userDao.findAll(); }

    public User updateUser(Long id, UserDto dto) {
        return userDao.update(id, dto);
    }

    public void deleteUser(Long id) { userDao.delete(id); }

    @Transactional
    public Address addAddress(Long userId, AddressDto dto) {
        // delegate to AddressDao
        return addressDao.create(dto);
    }

    public List<Address> listAddresses(Long userId) {
        return addressDao.findByUserId(userId);
    }

    public Address updateAddress(Long id, AddressDto dto) {
        return addressDao.update(id, dto);
    }

    public void deleteAddress(Long id) { addressDao.delete(id); }
}

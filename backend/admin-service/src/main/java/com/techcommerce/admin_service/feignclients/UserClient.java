package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.UserDto;
import com.techcommerce.admin_service.dto.AddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/api/users")
    @CircuitBreaker(name = "userService", fallbackMethod = "createUserFallback")
    @Retry(name = "userService")
    UserDto createUser(@RequestBody UserDto dto);

    default UserDto createUserFallback(UserDto dto, Throwable t) {
        return new UserDto();
    }

    @GetMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    UserDto getUser(@PathVariable Long id);

    default UserDto getUserFallback(Long id, Throwable t) {
        return new UserDto();
    }

    @GetMapping("/api/users")
    @CircuitBreaker(name = "userService", fallbackMethod = "listUsersFallback")
    @Retry(name = "userService")
    List<UserDto> listUsers();

    default List<UserDto> listUsersFallback(Throwable t) { return java.util.Collections.emptyList(); }

    @PutMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "updateUserFallback")
    @Retry(name = "userService")
    UserDto updateUser(@PathVariable Long id, @RequestBody UserDto dto);

    default UserDto updateUserFallback(Long id, UserDto dto, Throwable t) { return new UserDto(); }

    @DeleteMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "deleteUserFallback")
    @Retry(name = "userService")
    void deleteUser(@PathVariable Long id);

    default void deleteUserFallback(Long id, Throwable t) { }

    //============ ADDRESSES =============//

    @PostMapping("/api/users/{id}/addresses")
    @CircuitBreaker(name = "userService", fallbackMethod = "addAddressFallback")
    @Retry(name = "userService")
    AddressDto addAddress(@PathVariable Long id, @RequestBody AddressDto dto);

    default AddressDto addAddressFallback(Long id, AddressDto dto, Throwable t) { return new AddressDto(); }

    @GetMapping("/api/users/{id}/addresses")
    @CircuitBreaker(name = "userService", fallbackMethod = "listAddressesFallback")
    @Retry(name = "userService")
    List<AddressDto> listAddresses(@PathVariable Long id);

    default List<AddressDto> listAddressesFallback(Long id, Throwable t) { return java.util.Collections.emptyList(); }

    @PutMapping("/api/addresses/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "updateAddressFallback")
    @Retry(name = "userService")
    AddressDto updateAddress(@PathVariable Long id, @RequestBody AddressDto dto);

    default AddressDto updateAddressFallback(Long id, AddressDto dto, Throwable t) { return new AddressDto(); }

    @DeleteMapping("/api/addresses/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "deleteAddressFallback")
    @Retry(name = "userService")
    void deleteAddress(@PathVariable Long id);

    default void deleteAddressFallback(Long id, Throwable t) { }
}

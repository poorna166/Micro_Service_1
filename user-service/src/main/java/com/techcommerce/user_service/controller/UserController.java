package com.techcommerce.user_service.controller;

import com.techcommerce.user_service.dto.AddressDto;
import com.techcommerce.user_service.dto.UserDto;
import com.techcommerce.user_service.model.Address;
import com.techcommerce.user_service.model.User;
import com.techcommerce.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User API", description = "Operations for managing users and addresses")
public class UserController {

    private final com.techcommerce.user_service.service.UserServicePort userService;

    public UserController(com.techcommerce.user_service.service.UserServicePort userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a user", description = "Create a new user from a UserDto payload")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User payload", required = true) @RequestBody UserDto userDto) {
        User created = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get a user by id", description = "Retrieve a user by its id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
    return userService.getUser(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users")
    public List<User> listUsers() { return userService.listUsers(); }

    @Operation(summary = "Update a user", description = "Apply partial update to a user using UserDto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User patch payload") @RequestBody UserDto patch) {
        try {
            User u = userService.updateUser(id, patch);
            return ResponseEntity.ok(u);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Addresses
    @Operation(summary = "Add address for user", description = "Create an address for the given user id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address created"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/users/{id}/addresses")
    public ResponseEntity<Address> addAddress(@Parameter(description = "ID of the user", required = true) @PathVariable Long id,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Address payload", required = true) @RequestBody AddressDto addressDto) {
        try {
            Address a = userService.addAddress(id, addressDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(a);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{id}/addresses")
    public List<Address> listAddresses(@PathVariable Long id) { return userService.listAddresses(id); }

    @Operation(summary = "Update an address", description = "Update an address by id using AddressDto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/addresses/{id}")
    public ResponseEntity<Address> updateAddress(@Parameter(description = "ID of the address to update", required = true) @PathVariable Long id,
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Address patch payload") @RequestBody AddressDto patch) {
        try {
            Address a = userService.updateAddress(id, patch);
            return ResponseEntity.ok(a);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}

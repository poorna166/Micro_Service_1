package com.skinflex.auth_service.controller;

import com.skinflex.auth_service.entity.Role;
import com.skinflex.auth_service.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role saved = roleRepository.save(role);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }
}

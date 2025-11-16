package com.skinflex.auth_service.controller;

import com.skinflex.auth_service.util.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class JwkSetController {

    private final JwtTokenProvider jwtTokenProvider;

    public JwkSetController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> jwks() {
        Map<String, Object> keys = new HashMap<>();
        keys.put("keys", java.util.List.of(jwtTokenProvider.getJwk()));
        return ResponseEntity.ok(keys);
    }

    // convenience endpoint compatible with some consumers
    @GetMapping(value = "/certs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> certs() {
        return jwks();
    }
}

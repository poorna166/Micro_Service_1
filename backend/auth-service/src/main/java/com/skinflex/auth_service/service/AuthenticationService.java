package com.skinflex.auth_service.service;

import com.skinflex.auth_service.dto.LoginRequest;
import com.skinflex.auth_service.dto.RegisterRequest;
import com.skinflex.auth_service.dto.AuthResponse;
import com.skinflex.auth_service.entity.Role;
import com.skinflex.auth_service.entity.User;
import com.skinflex.auth_service.exception.AuthenticationException;
import com.skinflex.auth_service.repository.UserRepository;
import com.skinflex.auth_service.repository.RoleRepository;
import com.skinflex.auth_service.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final com.skinflex.auth_service.repository.RefreshTokenRepository refreshTokenRepository;

    public AuthenticationService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               JwtTokenProvider jwtTokenProvider,
                               PasswordEncoder passwordEncoder,
                               com.skinflex.auth_service.repository.RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        // Validate password match
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new AuthenticationException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    newRole.setDescription("Default user role");
                    return roleRepository.save(newRole);
                });
        user.setRoles(new HashSet<>(Set.of(userRole)));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Generate tokens
    String accessToken = jwtTokenProvider.generateToken(savedUser.getUsername(), savedUser.getEmail());
    String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername());

    // persist refresh token for revocation/validation
    com.skinflex.auth_service.entity.RefreshToken rt = new com.skinflex.auth_service.entity.RefreshToken();
    rt.setToken(refreshToken);
    rt.setUsername(savedUser.getUsername());
    rt.setExpiresAt(java.time.Instant.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirySeconds()));
    refreshTokenRepository.save(rt);

    return new AuthResponse(
        accessToken,
        refreshToken,
        jwtTokenProvider.getJwtExpirationMs(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        "User registered successfully"
    );
    }

    /**
     * Login user with credentials
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new AuthenticationException("User account is disabled");
        }

        log.info("User logged in successfully: {}", user.getUsername());

        // Generate tokens
    String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getEmail());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

    // persist refresh token
    com.skinflex.auth_service.entity.RefreshToken rt = new com.skinflex.auth_service.entity.RefreshToken();
    rt.setToken(refreshToken);
    rt.setUsername(user.getUsername());
    rt.setExpiresAt(java.time.Instant.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirySeconds()));
    refreshTokenRepository.save(rt);

    return new AuthResponse(
        accessToken,
        refreshToken,
        jwtTokenProvider.getJwtExpirationMs(),
        user.getUsername(),
        user.getEmail(),
        "Login successful"
    );
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * Refresh JWT token
     */
    public AuthResponse refreshToken(String refreshToken) {
    // ensure refresh token exists in DB
    com.skinflex.auth_service.entity.RefreshToken existing = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new AuthenticationException("Invalid or expired refresh token"));

    if (!jwtTokenProvider.validateToken(refreshToken)) {
        // cleanup stale token
        refreshTokenRepository.deleteByToken(refreshToken);
        throw new AuthenticationException("Invalid or expired refresh token");
    }

    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new AuthenticationException("User not found"));

    // rotate refresh token: delete old, create new
    refreshTokenRepository.deleteByToken(refreshToken);
    String newAccessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getEmail());
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
    com.skinflex.auth_service.entity.RefreshToken rt = new com.skinflex.auth_service.entity.RefreshToken();
    rt.setToken(newRefreshToken);
    rt.setUsername(user.getUsername());
    rt.setExpiresAt(java.time.Instant.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirySeconds()));
    refreshTokenRepository.save(rt);

    return new AuthResponse(
        newAccessToken,
        newRefreshToken,
        jwtTokenProvider.getJwtExpirationMs(),
        user.getUsername(),
        user.getEmail(),
        "Token refreshed successfully"
    );
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }
}

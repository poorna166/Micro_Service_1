package com.skinflex.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Basic reactive security chain: permit auth endpoints, require ADMIN for admin paths, authenticate others
        http
            .csrf().disable()
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/api/auth/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**",
                              "/api/**/v3/api-docs", "/api/**/swagger-ui.html", "/api/**/swagger-ui/**").permitAll()
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(reactiveJwtDecoder())
                    .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                )
            );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

    /**
     * Convert JWT claims into Spring GrantedAuthority collection.
     * Supports common claim shapes: realm_access.roles (Keycloak), roles, authorities.
     */
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Converter<Jwt, Collection<GrantedAuthority>>) jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Keycloak style: realm_access.roles -> ["admin"]
            Object realmAccess = jwt.getClaims().get("realm_access");
            if (realmAccess instanceof Map) {
                Object roles = ((Map<?, ?>) realmAccess).get("roles");
                if (roles instanceof List) {
                    for (Object r : (List<?>) roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()));
                    }
                }
            }

            // Generic 'roles' claim
            Object rolesClaim = jwt.getClaims().get("roles");
            if (rolesClaim instanceof List) {
                for (Object r : (List<?>) rolesClaim) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()));
                }
            } else if (rolesClaim instanceof String) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesClaim.toString().toUpperCase()));
            }

            // Generic 'authorities' claim
            Object auths = jwt.getClaims().get("authorities");
            if (auths instanceof List) {
                for (Object r : (List<?>) auths) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()));
                }
            }

            // Fallback: if no roles discovered, assign ROLE_USER to authenticated tokens
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return authorities;
        });

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}

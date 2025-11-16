package com.skinflex.api_gateway.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 5)
public class RoleHeaderWebFilter implements WebFilter {

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> ctx.getAuthentication())
        .flatMap(auth -> {
            if (auth instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            String primary = jwtAuth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .map(r -> r.replaceFirst("^ROLE_", ""))
                .findFirst()
                .orElse("USER");

            ServerWebExchange mutated = exchange.mutate()
                .request(exchange.getRequest().mutate().header("X-User-Role", primary).build())
                .build();
            return chain.filter(mutated);
            }
            return chain.filter(exchange);
        })
        // If there's no security context, just continue without mutation
        .switchIfEmpty(chain.filter(exchange));
    }
}

package com.skinflex.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RoleBasedAccessFilter extends AbstractGatewayFilterFactory<RoleBasedAccessFilter.Config> {

    public RoleBasedAccessFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String userRole = request.getHeaders().getOrEmpty("X-User-Role").stream()
                    .findFirst()
                    .orElse("USER");

            boolean hasRequiredRole = config.allowedRoles.stream()
                    .anyMatch(role -> role.equalsIgnoreCase(userRole));

            if (!hasRequiredRole) {
                return onError(exchange, HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        public java.util.List<String> allowedRoles;

        public void setAllowedRoles(java.util.List<String> allowedRoles) {
            this.allowedRoles = allowedRoles;
        }
    }
}

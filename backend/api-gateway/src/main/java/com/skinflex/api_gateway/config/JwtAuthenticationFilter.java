package com.skinflex.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Check if the Authorization header exists
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // Basic JWT payload inspection (no signature verification)
            if (token.isEmpty()) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            try {
                String[] parts = token.split("\\.");
                if (parts.length < 2) {
                    return onError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
                }
                String payload = parts[1];
                byte[] decoded = Base64.getUrlDecoder().decode(payload);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> claims = mapper.readValue(decoded, Map.class);

                // extract role (support 'role' or 'roles' or 'authorities')
                String role = "USER";
                if (claims.containsKey("role")) {
                    role = String.valueOf(claims.get("role"));
                } else if (claims.containsKey("roles")) {
                    Object r = claims.get("roles");
                    if (r instanceof java.util.List) role = ((java.util.List<?>) r).get(0).toString();
                    else role = r.toString();
                } else if (claims.containsKey("authorities")) {
                    Object r = claims.get("authorities");
                    if (r instanceof java.util.List) role = ((java.util.List<?>) r).get(0).toString();
                    else role = r.toString();
                }

                // add X-User-Role header to request for downstream filters
                ServerHttpRequest mutated = request.mutate()
                        .header("X-User-Role", role)
                        .build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
                return chain.filter(mutatedExchange);
            } catch (Exception ex) {
                return onError(exchange, "Invalid JWT token payload", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
    }
}

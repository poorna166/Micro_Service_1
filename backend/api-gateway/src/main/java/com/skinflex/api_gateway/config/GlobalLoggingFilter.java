package com.skinflex.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter extends AbstractGatewayFilterFactory<GlobalLoggingFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    public GlobalLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().toString();

            logger.info("Incoming request: {} {}", method, path);

            return chain.filter(exchange).doFinally(signal -> {
                long duration = System.currentTimeMillis() - startTime;
                int status = 0;
                if (exchange.getResponse().getStatusCode() != null) {
                    status = exchange.getResponse().getStatusCode().value();
                }
                logger.info("Outgoing response: {} {} - Status: {} - Duration: {}ms", 
                        method, path, status, duration);
            });
        };
    }

    public static class Config {
    }
}


package com.techcommerce.cart_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "product-service", url = "http://localhost:9001")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    ProductDto getProduct(@PathVariable("id") Long id);

    default ProductDto getProductFallback(Long id, Exception e) {
        // Return default product when service is unavailable
        ProductDto fallback = new ProductDto();
        fallback.setId(id);
        fallback.setName("Service Unavailable");
        fallback.setPrice(0.0);
        fallback.setStock(0);
        return fallback;
    }

    static class ProductDto {
        private Long id;
        private String name;
        private Double price;
        private Integer stock;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }
}

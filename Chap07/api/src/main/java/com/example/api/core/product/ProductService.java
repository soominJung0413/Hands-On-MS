package com.example.api.core.product;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface ProductService {

    Product createProduct(@RequestBody Product body);

    @GetMapping(
            value = "/product/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Mono<Product> getProduct(@PathVariable int productId);

    void deleteProduct(@PathVariable int productId);
}

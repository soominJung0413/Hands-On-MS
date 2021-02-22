package com.example.productservice.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<ProductEntity, String> {
    @Transactional(readOnly = true)
    Mono<ProductEntity> findByProductId(int productId);
}

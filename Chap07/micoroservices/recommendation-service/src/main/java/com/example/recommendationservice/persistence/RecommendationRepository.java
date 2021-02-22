package com.example.recommendationservice.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface RecommendationRepository extends R2dbcRepository<RecommendationEntity, String > {
    Flux<RecommendationEntity> findByProductId(int productId);
}

package com.example.api.core.recommendation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

public interface RecommendationService {

    Recommendation createRecommendation(@RequestBody Recommendation body);

    @GetMapping(
            value = "/recommendation",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Flux<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId);

    void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId);
}

package com.example.recommendationservice.services;

import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.recommendation.RecommendationService;
import com.example.recommendationservice.persistence.RecommendationEntity;
import com.example.recommendationservice.persistence.RecommendationRepository;
import com.example.util.exceptions.InvalidInputException;
import com.example.util.http.ServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j @AllArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;
    private final ServiceUtil serviceUtil;

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: "+body.getProductId());

        RecommendationEntity newEntity = mapper.apiToEntity(body);

        Mono<Recommendation> savedApi = repository.save(newEntity).onErrorMap(
                DataIntegrityViolationException.class,
                ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() +
                        ", Recommendation Id:" + body.getRecommendationId()))
                .map(e -> mapper.entityToApi(e));

        return savedApi.block();
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        return repository.findByProductId(productId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        log.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId)).block();
    }
}

package com.example.recommendationservice;

import com.example.recommendationservice.persistence.RecommendationEntity;
import com.example.recommendationservice.persistence.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
public class PersistenceTests {

    @Autowired
    private RecommendationRepository repository;

    private RecommendationEntity savedEntity;

    @BeforeEach
    public void setUpDb() {
        repository.deleteAll().block();

        RecommendationEntity entity = new RecommendationEntity(1,2,"a",3,"c");
        savedEntity = repository.save(entity).block();

        assertEqualsRecommendations(entity, savedEntity);
    }

    @Test
    public void create() {
        RecommendationEntity newEntity = new RecommendationEntity(1, 3, "a", 3, "c");
        repository.save(newEntity).block();

        RecommendationEntity foundEntity = repository.findById(newEntity.getId()).block();

        assertEquals(2,(long)repository.count().block());
    }

    @Test
    public void update() {
        savedEntity.setAuthor("a2");
        repository.save(savedEntity).block();

        RecommendationEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
    public void getByProductId() {
        List<RecommendationEntity> entityList = repository.findByProductId(savedEntity.getProductId()).collectList().block();

        assertThat(entityList.size()).isEqualTo(1);
        assertEqualsRecommendations(savedEntity, entityList.get(0));
    }

    @Test
    public void duplicateError() {
        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        StepVerifier.create(repository.save(entity)).expectError(DataIntegrityViolationException.class).verify();
    }

    @Test
    public void optimisticLockError() {

        RecommendationEntity entity1 = repository.findById(savedEntity.getId()).block();
        RecommendationEntity entity2 = repository.findById(savedEntity.getId()).block();

        entity1.setAuthor("a1");
        repository.save(entity1).block();

        try {
            entity2.setAuthor("a2");
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        }catch (OptimisticLockingFailureException ex){}

        RecommendationEntity updateEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updateEntity.getVersion());
        assertEquals("a1",updateEntity.getAuthor());
    }

    private void assertEqualsRecommendations(RecommendationEntity expectedEntity, RecommendationEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getRecommendationId(), actualEntity.getRecommendationId());
        assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
        assertEquals(expectedEntity.getRating(), actualEntity.getRating());
        assertEquals(expectedEntity.getContent(), actualEntity.getContent());
    }
}

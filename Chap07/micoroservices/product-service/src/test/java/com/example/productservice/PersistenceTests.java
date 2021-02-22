package com.example.productservice;

import com.example.productservice.persistence.ProductEntity;
import com.example.productservice.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import reactor.test.StepVerifier;

@DataR2dbcTest
public class PersistenceTests {

    @Autowired
    private ProductRepository repository;
    private ProductEntity savedEntity;

    @BeforeEach
    public void setUpDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        ProductEntity entity = new ProductEntity(1,"n",1);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(e -> {
                    savedEntity = e;
                    return areProductEqual(entity, savedEntity);
                }).verifyComplete();
    }

    @Test
    public void create() {
        ProductEntity newEntity = new ProductEntity(2,"n",2);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(e -> e.getProductId() == newEntity.getProductId())
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void update() {
        savedEntity.setName("n2");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(e -> e.getName().equals("n2")).verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(e -> e.getVersion() == 1 && e.getName().equals("n2")).verifyComplete();
    }

    @Test
    public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    public void getByProductId() {
        StepVerifier.create(repository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(e -> areProductEqual(savedEntity, e))
                .verifyComplete();
    }

    @Test
    public void duplicateError() {
        ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
        StepVerifier.create(repository.save(entity)).expectError(DataIntegrityViolationException.class).verify();
    }

    @Test
    public void optimisticLockError() {
        ProductEntity entity1 = repository.findById(savedEntity.getId()).block();
        ProductEntity entity2 = repository.findById(savedEntity.getId()).block();

        /**
         * 낙관적 잠금 확인
         */
        entity1.setName("n1");
        repository.save(entity1).block();

        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(e -> e.getVersion() == 1 && e.getName().equals("n1"))
                .verifyComplete();
    }

    private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
        return (expectedEntity.getId().equals(actualEntity.getId())) &&
                (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                (expectedEntity.getProductId() == actualEntity.getProductId()) &&
                (expectedEntity.getName().equals(actualEntity.getName())) &&
                (expectedEntity.getWeight() == actualEntity.getWeight());
    }
}

package com.example.productservice.services;

import com.example.api.core.product.Product;
import com.example.api.core.product.ProductService;
import com.example.productservice.persistence.ProductEntity;
import com.example.productservice.persistence.ProductRepository;
import com.example.util.exceptions.InvalidInputException;
import com.example.util.exceptions.NotFoundException;
import com.example.util.http.ServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j @AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService {

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Product createProduct(Product body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: "+body.getProductId());

        ProductEntity entity = mapper.apiToEntity(body);
        Mono<Product> savedProduct = repository.save(entity)
                .log()
                .onErrorMap(DataIntegrityViolationException.class, ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(e -> mapper.entityToApi(e));

        return savedProduct.block();
    }

    /**
     * Error 발생 시 맵핑 -> .onErrorMap
     * 값이 비었을 경우 로직 -> .switchIfEmpty
     * @param productId
     * @return
     */
    @Override
    public Mono<Product> getProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: "+productId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(a -> {a.setServiceAddress(serviceUtil.getServiceAddress()); return a;});

    }

    /**
     * 언제나 결과는 OK => 멱등성
     * @param productId
     */
    @Override
    public void deleteProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        log.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        /**
         * Mono<Mono<Void>> 해결을 위한 flatmap
         *
         *
         */
        repository.findByProductId(productId).map(e -> repository.delete(e)).flatMap(e -> e).block();
    }
}

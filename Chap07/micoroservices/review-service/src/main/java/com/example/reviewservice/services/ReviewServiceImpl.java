package com.example.reviewservice.services;

import com.example.api.core.review.Review;
import com.example.api.core.review.ReviewService;
import com.example.reviewservice.persistence.ReviewEntity;
import com.example.reviewservice.persistence.ReviewRepository;
import com.example.util.exceptions.InvalidInputException;
import com.example.util.http.ServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.function.Supplier;

@RestController
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;
    /**
     * 주목할 점은 이곳
     */
    private final Scheduler scheduler;



    @Override
    public Review createReview(Review body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: "+body.getProductId());

        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);

            log.debug("createReview: created a review entity: {}/{}",body.getProductId(),body.getReviewId());
            return mapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    /**
     * asyncFlux 메서드와 getByProductId 메서드가 핵심
     * @param productId
     * @return
     */
    @Override
    public Flux<Review> getReviews(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        log.info("Will get reviews for product with id={}",productId);

        return asyncFlux(() -> Flux.fromIterable(getByProductId(productId))).log();
    }

    /**
     * 영속 계층에서 정보 리스트를 가져오고 모델 컬렉션으로 변환 후 Review-Service 의 호스트 / 포트 주소를 저장
     * @param productId
     * @return
     */
    private List<Review> getByProductId(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = mapper.entityListToApiList(entityList);

        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        log.debug("getReviews: response size: {}",list.size());

        return list;
    }

    /**
     * Flux.defer 메서드로 Reactive Stream 을 나중에 구성 -> subscribeOn 으로 Scheduler 에 태워서 로직 실행
     * @param publisherSupplier
     * @param <T>
     * @return
     */
    private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }

    /**
     * 볼 것은 멱등성 이외에는 없음 -> 데이터가 없던 있던 언제나 OK 결과를 띄워야함.
     * @param productId
     */
    @Override
    public void deleteReviews(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: "+productId);

        log.debug("deleteReviews: tries to delete reviews for the product with productId: {}",productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}

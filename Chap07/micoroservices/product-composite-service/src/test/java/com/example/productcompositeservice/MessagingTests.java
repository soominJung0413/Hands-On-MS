package com.example.productcompositeservice;

import com.example.api.composite.product.ProductAggregate;
import com.example.api.composite.product.RecommendationSummary;
import com.example.api.composite.product.ReviewSummary;
import com.example.api.core.product.Product;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.review.Review;
import com.example.api.event.Event;
import com.example.productcompositeservice.services.ProductCompositeIntegration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.matcher.MessageQueueMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.BlockingQueue;

import static com.example.api.event.Event.Type.CREATE;
import static com.example.api.event.Event.Type.DELETE;
import static com.example.productcompositeservice.IsSameEvent.sameEventExceptCreatedAt;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagingTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductCompositeIntegration.MessageSources messageSources;

    // 테스트시 사용한 메시지를 모으는 클래스
    @Autowired
    private MessageCollector collector;


    BlockingQueue<Message<?>> queueProducts = null;
    BlockingQueue<Message<?>> queueRecommendations = null;
    BlockingQueue<Message<?>> queueReviews = null;

    @BeforeEach
    public void setUpQueue() {
        queueProducts = collector.forChannel(messageSources.outputProducts());
        queueRecommendations = collector.forChannel(messageSources.outputRecommendations());
        queueReviews = collector.forChannel(messageSources.outputReviews());
    }

    @Test
    public void createCompositeProduct1(){
        ProductAggregate productAggregate = new ProductAggregate(1,"name",1,null,null,null);
        postAndVerifyProduct(productAggregate, OK);

        // Message Channel 로 보낸 메세지는 BlockingQueue 에 저장됨
        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedEvent = new Event(CREATE, productAggregate.getProductId(),
                new Product(productAggregate.getProductId(),productAggregate.getName(),productAggregate.getWeight(),null));

        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        assertEquals(0, queueRecommendations.size());
        assertEquals(0, queueReviews.size());
    }

    @Test
    public void createCompositeProduct2() {
        ProductAggregate productAggregate = new ProductAggregate(1,"name",1,
                singletonList(new RecommendationSummary(1,"a",1,"c")),
                singletonList(new ReviewSummary(1,"a","s","c")),
                null);

        postAndVerifyProduct(productAggregate, OK);

        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedProductEvent = new Event(CREATE, productAggregate.getProductId(),
                new Product(productAggregate.getProductId(),productAggregate.getName(), productAggregate.getWeight(), null));
        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent))));

        // Recommendation 생성 요청에 대한 이벤트 게시 확인
        assertEquals(1, queueRecommendations.size());
        RecommendationSummary recommendationSummary = productAggregate.getRecommendations().get(0);
        Event<Integer, Product> expectedRecommendationEvent = new Event(CREATE, productAggregate.getProductId(),
                new Recommendation(productAggregate.getProductId(),recommendationSummary.getRecommendationId(),recommendationSummary.getAuthor(),
                        recommendationSummary.getRate(), recommendationSummary.getContent(), null));
        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));

        // Review 생성 요청에 대한 이벤트 게시 확인
        assertEquals(1, queueReviews.size());

        ReviewSummary rev = productAggregate.getReviews().get(0);

        Event<Integer, Product> expectedReviewEvent = new Event(CREATE, productAggregate.getProductId(),
                new Review(productAggregate.getProductId(), rev.getReviewId(), rev.getAuthor(), rev.getSubject(), rev.getContent(), null));
        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
    }

    @Test
    public void deleteCompositeProduct() {
        deleteAndVerifyProduct(1, OK);

        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedEvent = new Event(DELETE, 1, null);
        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        //recommendation 삭제 확인
        assertEquals(1, queueRecommendations.size());

        Event<Integer,Product> expectedRecommendationEvent = new Event(DELETE, 1 , null);
        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));
        
        //review 삭제 확인
        Assert.assertEquals(1, queueReviews.size());

        Event<Integer, Product> expectedReviewEvent = new Event(DELETE, 1, null);
        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
    }

    private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
        client.post()
                .uri("/product-composite")
                .body(Mono.just(compositeProduct), ProductAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/product-composite/" + productId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

}

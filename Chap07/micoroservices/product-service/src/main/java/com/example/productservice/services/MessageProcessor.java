package com.example.productservice.services;

import com.example.api.core.product.Product;
import com.example.api.core.product.ProductService;
import com.example.api.event.Event;
import com.example.util.exceptions.EventProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@Slf4j
@AllArgsConstructor
public class MessageProcessor {

    private final ProductService productService;

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Product> event) {
        log.info("Process message created at {}",event.getEventCreatedAt());


        switch (event.getEventType()) {
            case CREATE:
                Product product = event.getData();
                log.info("Create product with ID: {}",product.getProductId());
                productService.createProduct(product);
                break;

            case DELETE:
                int productId = event.getKey();
                log.info("Delete recommendations with ProductID: {}",productId);
                productService.deleteProduct(productId);
                break;

            default:
                String errorMessage = "Incorrect event type: "+ event.getEventType() +", expected a CREATE or DELETE event";
                log.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        log.info("Message processing done!");
    }
}

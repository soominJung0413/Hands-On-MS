package com.example.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor @Getter
public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final List<RecommendationSummary> recommendations;
    private final List<ReviewSummary> reviews;
    private final ServiceAddresses serviceAddresses;

    public ProductAggregate() {
        this.productId = 0;
        this.name = null;
        this.weight = 0;
        this.recommendations = null;
        this.reviews = null;
        this.serviceAddresses = null;
    }
}

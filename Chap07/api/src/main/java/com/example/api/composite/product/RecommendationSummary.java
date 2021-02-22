package com.example.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  하나의 Review 모델을 통합하기 위한 데이터만을 가진 축약 클래스
 */
@AllArgsConstructor
@Getter
public class RecommendationSummary {
    private final int recommendationId;
    private final String author;
    private final int rate;
    private final String content;

    public RecommendationSummary() {
        this.recommendationId = 0;
        this.author = null;
        this.rate = 0;
        this.content = null;
    }


}

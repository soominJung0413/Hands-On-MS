package com.example.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Review 모델을 축약하여 통합하기 위한 정보를 가진 축약클래스
 */
@Getter
public class ReviewSummary {
    private final int reviewId;
    private final String author;
    private final String subject;
    private final String content;

    public ReviewSummary() {
        this.reviewId = 0;
        this.author = null;
        this.subject = null;
        this.content = null;
    }

    public ReviewSummary(int reviewId, String author, String subject, String content) {
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }
}

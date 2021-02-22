package com.example.reviewservice.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "reviews", indexes = {@Index(name = "rsc_product_rc_idx", unique = true, columnList = "productId,reviewId")})
@NoArgsConstructor
public class ReviewEntity {

    /**
     * 키 값 생성을 JPA 에게 요청 중
     */
    @Id @GeneratedValue
    private int id;
    /**
     * @Version 의 경우 업데이트시의 데이터 무결성을 위해 JPA 가 지정하는 낙관적 잠금 번호이다.
     */
    @Version
    private int version;

    /**
     * JPA 가 생성하는 Id 는 외부에 노출되서는 아니한다.
     * 즉, 비즈니스 적 Id 만이 노출되어야한다.
     */
    private int productId;
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

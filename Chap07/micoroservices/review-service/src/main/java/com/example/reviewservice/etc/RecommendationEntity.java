package com.example.reviewservice.etc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Version;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Profile("docker")
@Entity(name = "recommendations")
@Table(indexes = {@Index(name = "rsc_product_rc_idx", unique = true, columnList = "productId,recommendationId")})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class RecommendationEntity {

    @Id
    private String id;

    @Version
    private Integer version;
    private int productId;
    private int recommendationId;
    private String author;
    private int rating;
    private String content;
}

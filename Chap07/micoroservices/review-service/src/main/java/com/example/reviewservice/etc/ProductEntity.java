package com.example.reviewservice.etc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Version;

import javax.persistence.*;

@Profile("docker")
@Entity(name = "products")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class ProductEntity {

    @Id
    private String id;
    @Version
    private Integer version;
    @Column(unique = true)
    private int productId;
    private String name;
    private int weight;
}

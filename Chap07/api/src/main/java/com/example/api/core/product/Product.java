package com.example.api.core.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter @NoArgsConstructor
public class Product {
    private int productId;
    private String name;
    private int weight;
    private String serviceAddress;
}

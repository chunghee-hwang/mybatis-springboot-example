package com.example.mybatis.demo.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Product {
    private Long prodId;
    private String prodName;
    private int prodPrice;
}

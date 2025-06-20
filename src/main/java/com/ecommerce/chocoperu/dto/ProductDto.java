package com.ecommerce.chocoperu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Long providerId;
    private Long categoryId;
    private String providerName;
    private String categoryName;
    private String imageUrl;
}

package com.ecommerce.chocoperu.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long productId;
    private int quantity;
    private double price;
}

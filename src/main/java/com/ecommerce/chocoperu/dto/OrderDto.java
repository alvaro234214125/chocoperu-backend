package com.ecommerce.chocoperu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private String status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}

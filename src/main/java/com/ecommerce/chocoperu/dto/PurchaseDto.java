package com.ecommerce.chocoperu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double totalPrice;
    private LocalDateTime purchaseDate;
    private Long buyerId;
    private String buyerName;
}

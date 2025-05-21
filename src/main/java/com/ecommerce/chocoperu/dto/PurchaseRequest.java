package com.ecommerce.chocoperu.dto;

import lombok.Data;

@Data
public class PurchaseRequest {
    private Long productId;
    private int quantity;
}

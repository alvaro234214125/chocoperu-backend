package com.ecommerce.chocoperu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private String status;
    private String transactionId;
}

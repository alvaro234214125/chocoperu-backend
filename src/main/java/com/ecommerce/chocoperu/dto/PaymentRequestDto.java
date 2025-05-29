package com.ecommerce.chocoperu.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentRequestDto {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    private String transactionId;
}

package com.ecommerce.chocoperu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceDto {
    private Long id;
    private Long orderId;
    private Double amount;
    private LocalDateTime issuedAt;
}

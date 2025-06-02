package com.ecommerce.chocoperu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentDto {
    private Long id;
    private Long orderId;
    private String carrier;
    private String trackingNumber;
    private String status;
    private LocalDateTime shippedAt;
}

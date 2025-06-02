package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.ShipmentDto;
import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.entity.Shipment;
import com.ecommerce.chocoperu.entity.ShipmentStatus;
import com.ecommerce.chocoperu.repository.OrderRepository;
import com.ecommerce.chocoperu.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    public Shipment createShipment(Long orderId, String carrier, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Shipment shipment = Shipment.builder()
                .order(order)
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .status(ShipmentStatus.valueOf("PENDING"))
                .shippedAt(LocalDateTime.now())
                .build();

        return shipmentRepository.save(shipment);
    }

    public ShipmentDto toDto(Shipment shipment) {
        return ShipmentDto.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .carrier(shipment.getCarrier())
                .trackingNumber(shipment.getTrackingNumber())
                .status(String.valueOf(shipment.getStatus()))
                .shippedAt(shipment.getShippedAt())
                .build();
    }
}

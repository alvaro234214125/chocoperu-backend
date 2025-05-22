package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.OrderDto;
import com.ecommerce.chocoperu.dto.OrderItemDto;
import com.ecommerce.chocoperu.entity.*;
import com.ecommerce.chocoperu.repository.OrderItemRepository;
import com.ecommerce.chocoperu.repository.OrderRepository;
import com.ecommerce.chocoperu.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public List<OrderDto> getOrdersByUser(User user) {
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::toDto).toList();
    }

    public OrderDto getOrderByIdAndUser(Long orderId, User user) {
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));
        return toDto(order);
    }

    @Transactional
    public OrderDto placeOrder(User user, OrderDto orderDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.valueOf("PENDING"));
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = orderDto.getItems().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(product.getPrice() * itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        double total = items.stream().mapToDouble(OrderItem::getPrice).sum();
        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);

        items.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(items);

        savedOrder.setItems(items);

        return toDto(savedOrder);
    }

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getStatus().toString());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toDto).toList();
    }
}

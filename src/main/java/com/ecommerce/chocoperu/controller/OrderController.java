package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.OrderDto;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDto orderDto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        OrderDto savedOrder = orderService.placeOrder(user, orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<List<OrderDto>> getUserOrders(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<OrderDto> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        OrderDto order = orderService.getOrderByIdAndUser(id, user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}

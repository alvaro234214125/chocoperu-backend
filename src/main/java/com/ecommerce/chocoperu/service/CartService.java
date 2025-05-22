package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.CartItemDto;
import com.ecommerce.chocoperu.dto.CartItemRequest;
import com.ecommerce.chocoperu.dto.OrderDto;
import com.ecommerce.chocoperu.dto.OrderItemDto;
import com.ecommerce.chocoperu.entity.*;
import com.ecommerce.chocoperu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItem addToCart(User user, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getQuantity() > product.getStock()) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        CartItem cartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        return cartItemRepository.save(cartItem);
    }

    public List<CartItemDto> listCartItems(User user) {
        return cartItemRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void removeItem(Long id, User user) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public CartItemDto toDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getProduct().getPrice())
                .totalPrice(item.getQuantity() * item.getProduct().getPrice())
                .build();
    }

    @Transactional
    public Order checkout(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty.");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .totalPrice(0.0)
                .build();
        order = orderRepository.save(order);

        double total = 0.0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItemRepository.save(orderItem);
            total += product.getPrice() * item.getQuantity();
        }

        order.setTotalPrice(total);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("not_provided")
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        return order;
    }

    public CartItem updateQuantity(Long cartItemId, int quantity, User user) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Product product = item.getProduct();
        if (quantity > product.getStock()) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setId(item.getId());
                    dto.setProductId(item.getProduct().getId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    return dto;
                }).toList();

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setItems(itemDtos);
        return dto;
    }
}

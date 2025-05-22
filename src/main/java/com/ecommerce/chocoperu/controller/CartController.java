package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.CartItemDto;
import com.ecommerce.chocoperu.dto.CartItemRequest;
import com.ecommerce.chocoperu.dto.OrderDto;
import com.ecommerce.chocoperu.entity.CartItem;
import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<List<CartItemDto>> listCart(Authentication authentication) {
        User user = getUser(authentication);
        return ResponseEntity.ok(cartService.listCartItems(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<CartItemDto> addToCart(@Valid @RequestBody CartItemRequest request, Authentication authentication) {
        User user = getUser(authentication);
        CartItem item = cartService.addToCart(user, request);
        return ResponseEntity.ok(cartService.toDto(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<Void> removeItem(@PathVariable Long id, Authentication authentication) {
        User user = getUser(authentication);
        cartService.removeItem(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = getUser(authentication);
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }

    private User getUser(Authentication auth) {
        return ((CustomUserDetails) auth.getPrincipal()).getUser();
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<OrderDto> checkoutCart(Authentication authentication) {
        User user = getUser(authentication);
        Order order = cartService.checkout(user);
        return ResponseEntity.ok(cartService.toDto(order));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<CartItemDto> updateQuantity(@PathVariable Long id, @RequestParam int quantity, Authentication authentication) {
        User user = getUser(authentication);
        CartItem item = cartService.updateQuantity(id, quantity, user);
        return ResponseEntity.ok(cartService.toDto(item));
    }

}

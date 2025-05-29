package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.PaymentRequestDto;
import com.ecommerce.chocoperu.dto.PaymentDto;
import com.ecommerce.chocoperu.entity.Payment;
import com.ecommerce.chocoperu.entity.PaymentStatus;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<PaymentDto> payOrder(@Valid @RequestBody PaymentRequestDto request) {
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.ok(paymentService.toDto(payment));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDto>> getAll() {
        List<PaymentDto> payments = paymentService.findAll()
                .stream().map(paymentService::toDto).toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<PaymentDto> getOne(@PathVariable Long id, Authentication auth) {
        return paymentService.findById(id)
                .filter(p -> {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                            p.getOrder().getUser().getId().equals(userDetails.getUser().getId());
                })
                .map(paymentService::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updateStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        Payment updated = paymentService.updateStatus(id, status);
        return ResponseEntity.ok(paymentService.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<List<PaymentDto>> getMyPayments(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        List<PaymentDto> payments = paymentService.findByUserId(userId)
                .stream().map(paymentService::toDto).toList();

        return ResponseEntity.ok(payments);
    }

}

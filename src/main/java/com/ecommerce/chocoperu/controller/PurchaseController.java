package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.PurchaseDto;
import com.ecommerce.chocoperu.dto.PurchaseRequest;
import com.ecommerce.chocoperu.entity.Purchase;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    @PostMapping
    public ResponseEntity<PurchaseDto> purchaseProduct(@RequestBody PurchaseRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User buyer = userDetails.getUser();

        Purchase purchase = purchaseService.makePurchase(buyer, request);

        PurchaseDto dto = purchaseService.toDto(purchase);
        return ResponseEntity.ok(dto);
    }
}

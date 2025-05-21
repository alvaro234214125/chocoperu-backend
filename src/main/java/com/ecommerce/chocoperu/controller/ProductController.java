package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.ProductDto;
import com.ecommerce.chocoperu.entity.Product;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> listAll() {
        return productService.findAll().stream()
                .map(productService::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getOne(@PathVariable Long id) {
        return productService.findById(id)
                .map(productService::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        Product product = productService.fromDto(dto, user);
        Product saved = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productService.isOwner(#id, authentication.principal))")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        return productService.findById(id).map(existing -> {
            Product updatedEntity = productService.fromDto(dto, existing.getProvider());
            updatedEntity.setId(id);
            Product updated = productService.save(updatedEntity);
            return ResponseEntity.ok(productService.toDto(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productService.isOwner(#id, authentication.principal))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

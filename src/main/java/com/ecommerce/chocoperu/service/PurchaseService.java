package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.PurchaseDto;
import com.ecommerce.chocoperu.dto.PurchaseRequest;
import com.ecommerce.chocoperu.entity.Product;
import com.ecommerce.chocoperu.entity.Purchase;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.repository.ProductRepository;
import com.ecommerce.chocoperu.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Purchase makePurchase(User buyer, PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int quantityRequested = request.getQuantity();

        if (quantityRequested <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        if (product.getStock() < quantityRequested) {
            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
        }

        product.setStock(product.getStock() - quantityRequested);
        productRepository.save(product);

        double total = product.getPrice() * quantityRequested;

        Purchase purchase = Purchase.builder()
                .buyer(buyer)
                .product(product)
                .quantity(quantityRequested)
                .totalPrice(total)
                .purchaseDate(LocalDateTime.now())
                .build();

        return purchaseRepository.save(purchase);
    }

    public PurchaseDto toDto(Purchase purchase) {
        return PurchaseDto.builder()
                .id(purchase.getId())
                .productId(purchase.getProduct().getId())
                .productName(purchase.getProduct().getName())
                .quantity(purchase.getQuantity())
                .totalPrice(purchase.getTotalPrice())
                .purchaseDate(purchase.getPurchaseDate())
                .buyerId(purchase.getBuyer().getId())
                .buyerName(purchase.getBuyer().getUsername())
                .build();
    }

}

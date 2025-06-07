package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.ProductDto;
import com.ecommerce.chocoperu.entity.Category;
import com.ecommerce.chocoperu.entity.Product;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.repository.ProductRepository;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("productService")
@RequiredArgsConstructor
public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long productId, Object principal) {
        if (principal instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();
            return productRepository.findById(productId)
                    .map(product -> product.getProvider() != null &&
                            product.getProvider().getId().equals(user.getId()))
                    .orElse(false);
        }
        return false;
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .providerId(product.getProvider().getId())
                .categoryId(product.getCategory().getId())
                .providerName(product.getProvider().getUsername())
                .categoryName(product.getCategory().getName())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public Product fromDto(ProductDto dto, User provider) {
        Category category = categoryService.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .stock(dto.getStock())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .provider(provider)
                .category(category)
                .build();
    }
}

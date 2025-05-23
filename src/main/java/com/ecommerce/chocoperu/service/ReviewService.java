package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.ReviewDto;
import com.ecommerce.chocoperu.dto.ReviewRequest;
import com.ecommerce.chocoperu.entity.*;
import com.ecommerce.chocoperu.repository.OrderRepository;
import com.ecommerce.chocoperu.repository.ProductRepository;
import com.ecommerce.chocoperu.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewDto addReview(User user, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        boolean hasPurchased = orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .filter(order -> order.getItems() != null)
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (!hasPurchased) {
            throw new RuntimeException("Solo puedes valorar productos que has comprado");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        updateProductRating(product);

        return toDto(saved);
    }

    public List<ReviewDto> getReviewsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return reviewRepository.findByProduct(product)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReviewDto updateReview(User user, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Valoración no encontrada"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes editar esta valoración");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review updated = reviewRepository.save(review);
        updateProductRating(review.getProduct());

        return toDto(updated);
    }

    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Valoración no encontrada"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes eliminar esta valoración");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProduct(product);
        if (reviews.isEmpty()) {
            product.setAverageRating(0.0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            product.setAverageRating(avg);
        }
        productRepository.save(product);
    }

    public ReviewDto toDto(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setProductId(r.getProduct().getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}

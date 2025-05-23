package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.ReviewDto;
import com.ecommerce.chocoperu.dto.ReviewRequest;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.security.CustomUserDetails;
import com.ecommerce.chocoperu.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<ReviewDto> addReview(@RequestBody ReviewRequest request, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(reviewService.addReview(user, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewRequest request, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(reviewService.updateReview(user, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        reviewService.deleteReview(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }
}

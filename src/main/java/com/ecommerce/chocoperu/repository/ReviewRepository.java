package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Review;
import com.ecommerce.chocoperu.entity.User;
import com.ecommerce.chocoperu.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
    Optional<Review> findByUserAndProduct(User user, Product product);
}

package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Product;
import com.ecommerce.chocoperu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProvider(User provider);
}

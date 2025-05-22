package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.CartItem;
import com.ecommerce.chocoperu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteByUser(User user);
}

package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
}

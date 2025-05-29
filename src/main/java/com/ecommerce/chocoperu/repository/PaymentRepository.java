package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_User_Id(Long userId);
    Optional<Payment> findByOrder(Order order);
}

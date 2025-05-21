package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}

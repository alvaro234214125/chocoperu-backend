package com.ecommerce.chocoperu.repository;

import com.ecommerce.chocoperu.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrderId(Long orderId);
    @Query("SELECT COUNT(i) FROM Invoice i WHERE YEAR(i.issuedAt) = :year")
    long countByIssuedAtYear(@Param("year") int year);
}

package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.InvoiceDto;
import com.ecommerce.chocoperu.entity.Invoice;
import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.repository.InvoiceRepository;
import com.ecommerce.chocoperu.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    public Invoice createInvoice(Long orderId, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());

        long countThisYear = invoiceRepository.countByIssuedAtYear(now.getYear());

        String sequence = String.format("%04d", countThisYear + 1);

        String invoiceNumber = "INV-" + year + "-" + sequence;

        Invoice invoice = Invoice.builder()
                .order(order)
                .totalAmount(amount)
                .issuedAt(now)
                .invoiceNumber(invoiceNumber)
                .build();

        return invoiceRepository.save(invoice);
    }


    public InvoiceDto toDto(Invoice invoice) {
        return InvoiceDto.builder()
                .id(invoice.getId())
                .orderId(invoice.getOrder().getId())
                .amount(invoice.getTotalAmount())
                .issuedAt(invoice.getIssuedAt())
                .build();
    }
}

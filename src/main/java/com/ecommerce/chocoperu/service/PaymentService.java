package com.ecommerce.chocoperu.service;

import com.ecommerce.chocoperu.dto.PaymentDto;
import com.ecommerce.chocoperu.entity.Order;
import com.ecommerce.chocoperu.entity.OrderStatus;
import com.ecommerce.chocoperu.entity.Payment;
import com.ecommerce.chocoperu.entity.PaymentStatus;
import com.ecommerce.chocoperu.repository.OrderRepository;
import com.ecommerce.chocoperu.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ecommerce.chocoperu.dto.PaymentRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment processPayment(PaymentRequestDto request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found for this order"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("This order has already been paid.");
        }

        String transactionId = (request.getTransactionId() == null || request.getTransactionId().trim().isEmpty())
                ? UUID.randomUUID().toString()
                : request.getTransactionId();


        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.SUCCESS);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    public Payment createPayment(Long orderId, String method, String transactionId, PaymentStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Optional<Payment> existingPayment = paymentRepository.findByOrder(order);
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already exists for this order.");
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(method)
                .transactionId(transactionId)
                .status(status)
                .build();

        return paymentRepository.save(payment);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        Payment updated = paymentRepository.save(payment);

        if (status == PaymentStatus.SUCCESS) {
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }

        return updated;
    }


    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    public PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .build();
    }

    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByOrder_User_Id(userId);
    }

}

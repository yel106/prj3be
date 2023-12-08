package com.example.prj3be.repository;

import com.example.prj3be.domain.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentUid(String orderId);
    Optional<Payment> findByPaymentKeyAndMember_Email(String paymentKey, String email);
    Slice<Payment> findAllByMember_Email(String email, Pageable pageable);
}

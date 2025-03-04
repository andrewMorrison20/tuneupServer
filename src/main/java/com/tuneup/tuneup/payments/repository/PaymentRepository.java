package com.tuneup.tuneup.payments.repository;

import com.tuneup.tuneup.payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(String status);
}


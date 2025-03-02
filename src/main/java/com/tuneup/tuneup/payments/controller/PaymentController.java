package com.tuneup.tuneup.payments.controller;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.payments.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a new payment
     * @param paymentDto details the payment to be created
     * @return
     */
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDto));
    }

    /** Fetch payments (with optional status filtering) */
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(paymentService.getPayments(status));
    }

    /** Mark multiple payments as paid */
    @PatchMapping("/mark-paid")
    public ResponseEntity<Void> markPaymentsAsPaid(@RequestBody List<Long> paymentIds) {
        paymentService.batchMarkPaymentsAsPaid(paymentIds);
        return ResponseEntity.noContent().build();
    }

    /** Upload an invoice file */
    @PostMapping("/upload-invoice")
    public ResponseEntity<String> uploadInvoice(@RequestParam("file") MultipartFile file) {
        String fileUrl = paymentService.uploadInvoice(file);
        return ResponseEntity.ok(fileUrl);
    }
}

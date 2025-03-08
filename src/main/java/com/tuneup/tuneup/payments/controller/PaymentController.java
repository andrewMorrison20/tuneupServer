package com.tuneup.tuneup.payments.controller;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.payments.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     *Retrieve payments for a given profile (and status)
     * @param profileId id of the profile to fetch payments for
     * @param status optional status of payments, e.g paid, due
     * @return List of payments as dtos
     */
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@RequestParam Long profileId, @RequestParam(required = false) String status) {
        return ResponseEntity.ok(paymentService.getPayments(profileId, status));
    }

    /**
     * Bacth update status of paid payments
     * @param paymentIds the ids of the payments to mark as paid
     * @return 204 status response
     */
    @PatchMapping("/mark-paid")
    public ResponseEntity<Void> markPaymentsAsPaid(@RequestBody List<Long> paymentIds) {
        paymentService.batchMarkPaymentsAsPaid(paymentIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload an invocie for a given payment
     * @param file
     * @return the url of the file
     */
    @PostMapping("/upload-invoice")
    public ResponseEntity<String> uploadInvoice(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = paymentService.uploadInvoice(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            // Log the error and return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading invoice: " + e.getMessage());
        } catch (Exception e) {
            // Catch unexpected exceptions and return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while uploading the invoice.");
        }
    }

    /**
     * Sends a reminder for a payment by updating the reminderSentOn field.
     * @param paymentId the ID of the payment to send a reminder for
     * @return 204 No Content if successful
     */
    @PatchMapping("/send-reminder/{paymentId}")
    public ResponseEntity<Void> sendPaymentReminder(@PathVariable Long paymentId) {
        paymentService.sendPaymentReminder(paymentId);
        return ResponseEntity.noContent().build();
    }
}

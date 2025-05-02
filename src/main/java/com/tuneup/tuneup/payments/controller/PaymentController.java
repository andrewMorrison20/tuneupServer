package com.tuneup.tuneup.payments.controller;
import com.tuneup.tuneup.payments.dtos.PaymentDto;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import com.tuneup.tuneup.payments.services.InvoiceService;
import com.tuneup.tuneup.payments.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * @return details of the newly created payment as dataTransferObject (dto)
     */
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDto));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePayments(@RequestBody List<Long> paymentIds) {
        paymentService.deletePayments(paymentIds);
        return ResponseEntity.noContent().build();
    }


    /**
     * Retrieve paginated payments for a given profile (and status)
     * @param profileId ID of the profile to fetch payments for
     * @param status optional status filter (Paid, Due, Overdue)
     * @param pageable pagination and sorting information
     * @return Paginated list of payments as DTOs
     */
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getPayments(
            @RequestParam Long profileId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Long profileFilterId,
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PaymentDto> payments = paymentService.getPayments(profileId, status, profileFilterId, pageable);
        return ResponseEntity.ok(payments);
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

    /**
     * get the invoice for a given payment
     * @param paymentId id of the payment to fetch invoice for
     * @return bye array of the file
     */
    @GetMapping("/invoice/{paymentId}")
    public ResponseEntity<byte[]> getPaymentInvoice(@PathVariable Long paymentId){
        byte[] invoice = paymentService.getPaymentInvoice(paymentId);
        return ResponseEntity.ok().body(invoice);
    }
}

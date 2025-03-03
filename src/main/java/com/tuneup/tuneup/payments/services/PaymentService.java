package com.tuneup.tuneup.payments.services;

import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.payments.mappers.PaymentMapper;
import com.tuneup.tuneup.payments.repository.PaymentRepository;
import com.tuneup.tuneup.tuitions.TuitionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;
    private final TuitionService  tuitionService;

    public PaymentService(PaymentRepository paymentRepository, InvoiceService invoiceService, PaymentMapper paymentMapper, TuitionService tuitionService) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
        this.paymentMapper = paymentMapper;
        this.tuitionService = tuitionService;
    }

    /**
     * Create a new payment
     * @param paymentDto details of the payment to create
     * @return
     */
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setTuition(tuitionService.findById(paymentDto.getTuitionId()));
        payment.setStatus("Due");
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    /**
     * Retrieve payments from the db for a given profile
     * @param status
     * @param profileId the profile to retrieve payments for
     * @return the list of payments as dtos
     */
    public List<PaymentDto> getPayments(Long profileId,String status) {

        return null;
    }

    /**
     * Update batch of payment statuses
     * @param paymentIds poayments to update
     */
    public void batchMarkPaymentsAsPaid(List<Long> paymentIds) {
        List<Payment> payments = paymentRepository.findAllById(paymentIds);
        payments.forEach(payment -> payment.setStatus("Paid"));
        paymentRepository.saveAll(payments);
    }

    /**
     * Upload an invoice file to cloud storage
     * @param file to upload
     * @return the file name url
     */
    public String uploadInvoice(MultipartFile file) throws IOException {
        // Simulated file upload (replace with actual storage logic)
        return invoiceService.uploadInvoice(file);
    }
}

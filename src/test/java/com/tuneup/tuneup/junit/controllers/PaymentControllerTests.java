package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.payments.controller.PaymentController;
import com.tuneup.tuneup.payments.dtos.PaymentDto;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import com.tuneup.tuneup.payments.services.InvoiceService;
import com.tuneup.tuneup.payments.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTests {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    private PaymentDto dto1;
    private PaymentDto dto2;

    @BeforeEach
    void setUp() {
        dto1 = new PaymentDto();
        dto1.setId(1L);
        dto1.setStatus(PaymentStatus.DUE);

        dto2 = new PaymentDto();
        dto2.setId(2L);
        dto2.setStatus(PaymentStatus.PAID);
    }

    @Test
    void createPayment_ReturnsDto() {
        when(paymentService.createPayment(dto1)).thenReturn(dto1);
        ResponseEntity<PaymentDto> resp = controller.createPayment(dto1);
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto1, resp.getBody());
        verify(paymentService).createPayment(dto1);
    }

    @Test
    void deletePayments_NoContent() {
        List<Long> ids = List.of(10L, 20L);
        doNothing().when(paymentService).deletePayments(ids);
        ResponseEntity<Void> resp = controller.deletePayments(ids);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(paymentService).deletePayments(ids);
    }

    @Test
    void getPayments_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "dueDate");
        Page<PaymentDto> page = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);
        when(paymentService.getPayments(5L, PaymentStatus.DUE, 99L, pageable)).thenReturn(page);

        ResponseEntity<Page<PaymentDto>> resp = controller.getPayments(5L, PaymentStatus.DUE, 99L, pageable);
        assertEquals(200, resp.getStatusCodeValue());
        Page<PaymentDto> body = resp.getBody();
        assertNotNull(body);
        assertEquals(2, body.getTotalElements());
        assertTrue(body.getContent().containsAll(Arrays.asList(dto1, dto2)));
        verify(paymentService).getPayments(5L, PaymentStatus.DUE, 99L, pageable);
    }

    @Test
    void markPaymentsAsPaid_NoContent() {
        List<Long> ids = List.of(11L, 22L);
        doNothing().when(paymentService).batchMarkPaymentsAsPaid(ids);
        ResponseEntity<Void> resp = controller.markPaymentsAsPaid(ids);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(paymentService).batchMarkPaymentsAsPaid(ids);
    }

    @Test
    void uploadInvoice_Success() throws IOException {
        byte[] content = "data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file","inv.pdf","application/pdf",content);
        when(paymentService.uploadInvoice(file)).thenReturn("http://url/inv.pdf");

        ResponseEntity<String> resp = controller.uploadInvoice(file);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("http://url/inv.pdf", resp.getBody());
        verify(paymentService).uploadInvoice(file);
    }

    @Test
    void uploadInvoice_IOException() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file","inv.pdf","application/pdf",new byte[0]);
        when(paymentService.uploadInvoice(file)).thenThrow(new IOException("fail"));

        ResponseEntity<String> resp = controller.uploadInvoice(file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().contains("Error uploading invoice: fail"));
    }

    @Test
    void uploadInvoice_GenericException() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file","inv.pdf","application/pdf",new byte[0]);
        when(paymentService.uploadInvoice(file)).thenThrow(new RuntimeException("oops"));

        ResponseEntity<String> resp = controller.uploadInvoice(file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("An unexpected error occurred while uploading the invoice.", resp.getBody());
    }

    @Test
    void sendPaymentReminder_NoContent() {
        doNothing().when(paymentService).sendPaymentReminder(55L);
        ResponseEntity<Void> resp = controller.sendPaymentReminder(55L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(paymentService).sendPaymentReminder(55L);
    }

    @Test
    void getPaymentInvoice_ReturnsBytes() {
        byte[] data = {1,2,3};
        when(paymentService.getPaymentInvoice(77L)).thenReturn(data);
        ResponseEntity<byte[]> resp = controller.getPaymentInvoice(77L);
        assertEquals(200, resp.getStatusCodeValue());
        assertArrayEquals(data, resp.getBody());
        verify(paymentService).getPaymentInvoice(77L);
    }
}

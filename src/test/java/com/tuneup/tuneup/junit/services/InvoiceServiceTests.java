package com.tuneup.tuneup.junit.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.tuneup.tuneup.payments.entities.Payment;
import com.tuneup.tuneup.payments.repository.PaymentRepository;
import com.tuneup.tuneup.payments.services.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTests {

    private InvoiceService invoiceService;
    private PaymentRepository paymentRepository;
    private Storage storageMock;
    private Bucket bucketMock;
    private Blob blobMock;

    @BeforeEach
    void setUp() throws IOException {
        paymentRepository = mock(PaymentRepository.class);
        invoiceService = new InvoiceService(paymentRepository);
        storageMock = mock(Storage.class);
        bucketMock = mock(Bucket.class);
        blobMock = mock(Blob.class);
        ReflectionTestUtils.setField(invoiceService, "storage", storageMock);
        ReflectionTestUtils.setField(invoiceService, "bucketName", "dummyBucket");
    }

    @Test
    void testUploadInvoice_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String originalFilename = "test.pdf";
        byte[] fileBytes = "dummyData".getBytes();
        String contentType = "application/pdf";
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);
        when(storageMock.get("dummyBucket")).thenReturn(bucketMock);
        when(bucketMock.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(blobMock);
        String resultFilename = invoiceService.uploadInvoice(file);
        assertTrue(resultFilename.startsWith("invoices/"));
        assertTrue(resultFilename.endsWith("-" + originalFilename));
        verify(bucketMock).create(eq(resultFilename), eq(fileBytes), eq(contentType));
    }

    @Test
    void testUpdateInvoice_paymentNotFound() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("update.pdf");
        when(file.getBytes()).thenReturn("data".getBytes());
        when(file.getContentType()).thenReturn("application/pdf");
        when(paymentRepository.findById(123L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(file, 123L));
        assertEquals("Payment not found: 123", ex.getMessage());
    }

    @Test
    void testUpdateInvoice_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String originalFilename = "update.pdf";
        byte[] fileBytes = "updateData".getBytes();
        String contentType = "application/pdf";
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);
        Payment payment = new Payment();
        payment.setInvoiceUrl(null);
        when(paymentRepository.findById(456L)).thenReturn(Optional.of(payment));
        when(storageMock.get("dummyBucket")).thenReturn(bucketMock);
        when(bucketMock.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(blobMock);
        String mediaLink = "http://media.link/" + UUID.randomUUID().toString();
        when(blobMock.getMediaLink()).thenReturn(mediaLink);
        String resultMediaLink = invoiceService.updateInvoice(file, 456L);
        assertEquals(mediaLink, resultMediaLink);
        assertEquals(mediaLink, payment.getInvoiceUrl());
        verify(paymentRepository).save(payment);
    }

    @Test
    void testDownloadInvoiceByFileName_success() {
        String fileName = "invoices/sample-file.pdf";
        byte[] content = "invoice content".getBytes();
        when(storageMock.get("dummyBucket")).thenReturn(bucketMock);
        when(bucketMock.get(fileName)).thenReturn(blobMock);
        when(blobMock.getContent()).thenReturn(content);
        byte[] resultContent = invoiceService.downloadInvoiceByFileName(fileName);
        assertArrayEquals(content, resultContent);
    }

    @Test
    void testDownloadInvoiceByFileName_fileNotFound() {
        String fileName = "invoices/missing-file.pdf";
        when(storageMock.get("dummyBucket")).thenReturn(bucketMock);
        when(bucketMock.get(fileName)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> invoiceService.downloadInvoiceByFileName(fileName));
        assertEquals("Invoice not found for file: " + fileName, ex.getMessage());
    }
}

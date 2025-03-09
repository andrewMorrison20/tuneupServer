package com.tuneup.tuneup.payments.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class InvoiceService {

    @Value("${gcs.invoice.bucket.name}")
    private String bucketName;
    private final Storage storage;
    private final PaymentRepository paymentRepository;

    public InvoiceService(PaymentRepository paymentRepository) throws IOException {
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("tuneup-cloud-key.json");
        if (credentialsStream == null) {
            throw new IOException("Service account key file not found in classpath: tuneup-cloud-key.json");
        }

        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(credentialsStream))
                .build()
                .getService();
        this.paymentRepository = paymentRepository;
    }

    /**
     * Uploads an invoice file before payment creation.
     * This does NOT link the invoice to a payment yet.
     *
     * @param file the invoice file
     * @return the uploaded invoice URL
     * @throws IOException if an error occurs during upload
     */
    public String uploadInvoice(MultipartFile file) throws IOException {
        String filename = "invoices/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(filename, file.getBytes(), file.getContentType());

        return filename;
    }

    /**
     * Updates the invoice for an existing payment.
     *
     * @param file      the new invoice file
     * @param paymentId the payment ID to associate the invoice with
     * @return the new invoice file URL
     * @throws IOException if an error occurs during upload
     */
    public String updateInvoice(MultipartFile file, Long paymentId) throws IOException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        String filename = "invoices/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(filename, file.getBytes(), file.getContentType());

        // Update the payment with the new invoice URL
        payment.setInvoiceUrl(blob.getMediaLink());
        paymentRepository.save(payment);

        return blob.getMediaLink();
    }



    /**
     * Downloads an invoice by its stored filename.
     *
     * @param fileName the stored filename of the invoice (e.g., "invoices/abc123-file.pdf")
     * @return the file content as a byte array
     */
    public byte[] downloadInvoiceByFileName(String fileName) {
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.get(fileName);

        if (blob == null) {
            throw new RuntimeException("Invoice not found for file: " + fileName);
        }

        return blob.getContent();
    }
}

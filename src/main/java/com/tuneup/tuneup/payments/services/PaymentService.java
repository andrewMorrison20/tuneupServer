package com.tuneup.tuneup.payments.services;

import com.tuneup.tuneup.availability.services.LessonService;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.NotificationType;
import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import com.tuneup.tuneup.payments.mappers.PaymentMapper;
import com.tuneup.tuneup.payments.repository.PaymentRepository;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.tuitions.Tuition;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;
    private final ProfileService profileService;
    private final TuitionService  tuitionService;
    private final LessonService lessonService;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(PaymentRepository paymentRepository,
                          InvoiceService invoiceService,
                          PaymentMapper paymentMapper,
                          ProfileService profileService,
                          TuitionService tuitionService,
                          LessonService lessonService,
                          ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
        this.paymentMapper = paymentMapper;
        this.profileService = profileService;
        this.tuitionService = tuitionService;
        this.lessonService = lessonService;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void checkOverduePaymentsOnStartup() {
        markOverduePayments();
    }


    /**
     * Create a new payment
     * @param paymentDto details of the payment to create
     * @return
     */
    public PaymentDto createPayment(PaymentDto paymentDto) {
        if(paymentRepository.existsByLessonId(paymentDto.getLessonId())){
            throw new ValidationException("Payment for this lesso already Exists!");
        }
        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setLesson(lessonService.findLessonById(paymentDto.getLessonId()));
        payment.setTuition(tuitionService.findById(paymentDto.getTuitionId()));
        payment.setStatus(PaymentStatus.DUE);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    /**
     * Retrieve paginated payments from the database for a given profile
     * @param profileId the profile to retrieve payments for
     * @param status optional status of the payments to fetch
     * @param pageable pagination information
     * @return paginated list of payments as DTOs
     */
    public Page<PaymentDto> getPayments(Long profileId, PaymentStatus status, Long filterProfileId, Pageable pageable) {
        Profile profile = profileService.fetchProfileEntityInternal(profileId);

        if (profile.getProfileType().equals(ProfileType.TUTOR)) {
            return paymentRepository.findAllByTutorId(profile.getId(), status, filterProfileId, pageable);
        } else {
            return paymentRepository.findAllByStudentId(profile.getId(), status, filterProfileId, pageable);
        }
    }


    /**
     * Update batch of payment statuses
     * @param paymentIds payments to update
     */
    @Transactional
    public void batchMarkPaymentsAsPaid(List<Long> paymentIds) {
        List<Payment> payments = paymentRepository.findAllById(paymentIds);
        payments.forEach(payment -> payment.setStatus(PaymentStatus.PAID));
        payments.forEach(payment -> payment.setPaidOn(LocalDateTime.now()));
        paymentRepository.saveAll(payments);
        payments.forEach(this::publishPaidNotification);
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

    /**
     * Sends a reminder notification for a payment
     * @param paymentId the id of the payment to generarte notification for
     */
    public void sendPaymentReminder(Long paymentId) {
       Payment payment =  paymentRepository.findById(paymentId).orElseThrow(()-> new ValidationException("No Payment found for id : " + paymentId));
       Profile student = payment.getTuition().getStudent();
       Long userId = student.getAppUser().getId();

       //event here to trigger notification!
        if(payment.getReminderSentOn() != null){
            throw new ValidationException("Reminder already sent for payment id : " + paymentId);
        }
        payment.setReminderSentOn(LocalDateTime.now());
        paymentRepository.save(payment);

        eventPublisher.publishEvent(
                new NotificationEvent(this, userId, NotificationType.PAYMENT_OVERDUE, "Reminder: You have an outstanding payment due on: " + payment.getDueDate())
        );
    }

    /**
     * Runs every day at midnight to mark overdue payments
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs at 12:00 AM daily
    public void markOverduePayments() {
        LocalDateTime today = LocalDate.now().atStartOfDay();

        // Fetch all payments that are Due but past their due date
        List<Payment> overduePayments = paymentRepository.findDuePaymentsPastDueDate(today);

        if (!overduePayments.isEmpty()) {
            overduePayments.forEach(payment -> payment.setStatus(PaymentStatus.OVERDUE));
            paymentRepository.saveAll(overduePayments);
            System.out.println("Updated " + overduePayments.size() + " payments to OVERDUE");
            //Only publish notifications once saved, not making this transactional since runs everynight and any missed will get picked up in subsequent run
            overduePayments.forEach(this::publishOverdueNotification);
        }
    }

    /**
     * delete  a set of payments
     * @param paymentIds the ids of the payments to delete
     */
    @Transactional
    public void deletePayments(List<Long> paymentIds) {
        paymentRepository.deleteAllById(paymentIds);
    }

    /**
     * Get the onvoice file for a payment by its id
     * @param paymentId id of the payment to fetch invoice for
     * @return the invoice file
     */
    public byte[] getPaymentInvoice(Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () ->new ValidationException("No payment fort id : " + paymentId)
        );

        byte[] file = invoiceService.downloadInvoiceByFileName(payment.getInvoiceUrl());
        return file;
    }

    /**
     * publish notification evetns for all parties when a payment is marked as overdue
     * @param payment the payment which is overdue
     */
    private void publishOverdueNotification(Payment payment) {

        Tuition tuition = payment.getTuition();
        Profile student = tuition.getStudent();
        Profile tutor = tuition.getTutor();

        Long studentUserId = student.getAppUser().getId();
        Long tutorUserId = tutor.getAppUser().getId();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String tutorMessage = "Payment due on " + payment.getDueDate().format(dateFormatter) + "for : "+ student.getDisplayName() +" is now overdue.See your payments dashboard for details";
        String studentMessage = "Payment due on " + payment.getDueDate().format(dateFormatter) + "for : "+ tutor.getDisplayName() +" is now overdue.See your payments dashboard for details";

        eventPublisher.publishEvent(
                new NotificationEvent(this, tutorUserId, NotificationType.PAYMENT_OVERDUE, tutorMessage)
        );

        eventPublisher.publishEvent(
                new NotificationEvent(this,studentUserId, NotificationType.PAYMENT_OVERDUE,studentMessage)
        );
    }

    /**
     * Publish a notification when a payment is marked as paid
     * @param payment thepayment that has been updated to paid
     */
    private void publishPaidNotification(Payment payment) {
        Tuition tuition = payment.getTuition();
        Profile student = tuition.getStudent();
        Profile tutor = tuition.getTutor();

        Long studentUserId = student.getAppUser().getId();
        Long tutorUserId = tutor.getAppUser().getId();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String formattedDueDate = payment.getDueDate().format(dateFormatter);

        // Construct the notification message
        String message = "Payment due on " + formattedDueDate + " has been marked as PAID. See your payments dashboard for details.";

        // Publish notification events for both parties
        eventPublisher.publishEvent(
                new NotificationEvent(this, studentUserId, NotificationType.PAYMENT_MADE, message)
        );
        eventPublisher.publishEvent(
                new NotificationEvent(this, tutorUserId, NotificationType.PAYMENT_MADE, message)
        );
    }
}

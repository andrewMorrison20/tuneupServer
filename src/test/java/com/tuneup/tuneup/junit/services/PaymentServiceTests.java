package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.availability.entities.Lesson;
import com.tuneup.tuneup.availability.services.LessonService;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.payments.entities.Payment;
import com.tuneup.tuneup.payments.dtos.PaymentDto;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import com.tuneup.tuneup.payments.mappers.PaymentMapper;
import com.tuneup.tuneup.payments.repository.PaymentRepository;
import com.tuneup.tuneup.payments.services.InvoiceService;
import com.tuneup.tuneup.payments.services.PaymentService;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private ProfileService profileService;

    @Mock
    private TuitionService tuitionService;

    @Mock
    private LessonService lessonService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDto paymentDto;
    private Payment payment;
    private Tuition tuition;
    private Profile student;
    private Profile tutor;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto();
        paymentDto.setLessonId(101L);
        paymentDto.setTuitionId(201L);

        payment = new Payment();
        payment.setDueDate(LocalDateTime.now().plusDays(7));
        payment.setStatus(PaymentStatus.DUE);

        tuition = new Tuition();
        student = new Profile();
        tutor = new Profile();
        student.setProfileType(ProfileType.STUDENT);
        tutor.setProfileType(ProfileType.TUTOR);
        student.setDisplayName("Student Name");
        tutor.setDisplayName("Tutor Name");

       AppUser studentAppUser =  new AppUser();
       AppUser tutorAppUser = new AppUser();

       tutor.setAppUser(tutorAppUser);
       student.setAppUser(studentAppUser);

       tuition.setStudent(student);
       tuition.setTutor(tutor);
    }

    @Test
    void testCheckOverduePaymentsOnStartup_noOverduePayments() {
        when(paymentRepository.findDuePaymentsPastDueDate(any())).thenReturn(Collections.emptyList());
        paymentService.checkOverduePaymentsOnStartup();
        verify(paymentRepository).findDuePaymentsPastDueDate(any());
        verify(paymentRepository, never()).saveAll(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testCheckOverduePaymentsOnStartup_withOverduePayments() {
        Payment overduePayment = new Payment();
        overduePayment.setDueDate(LocalDateTime.now().minusDays(1));
        overduePayment.setStatus(PaymentStatus.DUE);
        overduePayment.setTuition(tuition);
        overduePayment.setInvoiceUrl("invoice123");
        List<Payment> overduePayments = Collections.singletonList(overduePayment);
        when(paymentRepository.findDuePaymentsPastDueDate(any())).thenReturn(overduePayments);
        paymentService.checkOverduePaymentsOnStartup();
        assertEquals(PaymentStatus.OVERDUE, overduePayment.getStatus());
        verify(paymentRepository).saveAll(overduePayments);
        verify(eventPublisher, times(2)).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void testCreatePayment_whenPaymentAlreadyExists() {
        when(paymentRepository.existsByLessonId(paymentDto.getLessonId())).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> paymentService.createPayment(paymentDto));
        assertEquals("Payment for this lesson already Exists!", ex.getMessage());
    }

    @Test
    void testCreatePayment_success() {
        when(paymentRepository.existsByLessonId(paymentDto.getLessonId())).thenReturn(false);
        when(paymentMapper.toEntity(paymentDto)).thenReturn(payment);
        when(lessonService.findLessonById(paymentDto.getLessonId())).thenReturn(new Lesson());
        when(tuitionService.findById(paymentDto.getTuitionId())).thenReturn(tuition);
        when(paymentRepository.save(payment)).thenReturn(payment);
        PaymentDto returnedDto = new PaymentDto();
        when(paymentMapper.toDto(payment)).thenReturn(returnedDto);
        PaymentDto result = paymentService.createPayment(paymentDto);
        assertEquals(returnedDto, result);
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void testGetPayments_forTutor() {
        Profile tutorProfile = new Profile();
        tutorProfile.setId(300L);
        tutorProfile.setProfileType(ProfileType.TUTOR);
        when(profileService.fetchProfileEntityInternal(300L)).thenReturn(tutorProfile);
        Page<PaymentDto> page = new PageImpl<>(Collections.emptyList());
        when(paymentRepository.findAllByTutorId(300L, PaymentStatus.DUE, 400L, Pageable.unpaged())).thenReturn(page);
        Page<PaymentDto> result = paymentService.getPayments(300L, PaymentStatus.DUE, 400L, Pageable.unpaged());
        assertEquals(page, result);
    }

    @Test
    void testGetPayments_forStudent() {
        Profile studentProfile = new Profile();
        studentProfile.setId(500L);
        studentProfile.setProfileType(ProfileType.STUDENT);
        when(profileService.fetchProfileEntityInternal(500L)).thenReturn(studentProfile);
        Page<PaymentDto> page = new PageImpl<>(Collections.emptyList());
        when(paymentRepository.findAllByStudentId(500L, PaymentStatus.PAID, 600L, Pageable.unpaged())).thenReturn(page);
        Page<PaymentDto> result = paymentService.getPayments(500L, PaymentStatus.PAID, 600L, Pageable.unpaged());
        assertEquals(page, result);
    }

    @Test
    void testBatchMarkPaymentsAsPaid() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();

        p1.setTuition(tuition);
        p2.setTuition(tuition);

        p1.setDueDate(LocalDateTime.now());
        p2.setDueDate(LocalDateTime.now());

        List<Payment> payments = Arrays.asList(p1, p2);
        when(paymentRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(payments);
        paymentService.batchMarkPaymentsAsPaid(Arrays.asList(1L, 2L));
        assertEquals(PaymentStatus.PAID, p1.getStatus());
        assertEquals(PaymentStatus.PAID, p2.getStatus());
        assertNotNull(p1.getPaidOn());
        assertNotNull(p2.getPaidOn());
        verify(paymentRepository).saveAll(payments);
        verify(eventPublisher, times(4)).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void testUploadInvoice() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(invoiceService.uploadInvoice(file)).thenReturn("http://invoice.url/test.pdf");
        String result = paymentService.uploadInvoice(file);
        assertEquals("http://invoice.url/test.pdf", result);
    }

    @Test
    void testSendPaymentReminder_success() {
        Payment paymentReminder = new Payment();
        paymentReminder.setDueDate(LocalDateTime.now().plusDays(3));
        paymentReminder.setReminderSentOn(null);
        Tuition tuit = new Tuition();
        Profile std = new Profile();
        std.setDisplayName("Student");
        com.tuneup.tuneup.users.model.AppUser studentAppUser = mock(com.tuneup.tuneup.users.model.AppUser.class);
        when(studentAppUser.getId()).thenReturn(777L);
        std.setAppUser(studentAppUser);
        tuit.setStudent(std);
        paymentReminder.setTuition(tuit);
        when(paymentRepository.findById(999L)).thenReturn(Optional.of(paymentReminder));
        paymentService.sendPaymentReminder(999L);
        assertNotNull(paymentReminder.getReminderSentOn());
        verify(paymentRepository).save(paymentReminder);
        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void testSendPaymentReminder_alreadySent() {
        Payment paymentReminder = new Payment();
        paymentReminder.setDueDate(LocalDateTime.now().plusDays(3));
        paymentReminder.setReminderSentOn(LocalDateTime.now().minusDays(1));
        Tuition tuit = new Tuition();
        Profile std = new Profile();
        std.setDisplayName("Student");
        com.tuneup.tuneup.users.model.AppUser studentAppUser = mock(com.tuneup.tuneup.users.model.AppUser.class);
        when(studentAppUser.getId()).thenReturn(777L);
        std.setAppUser(studentAppUser);
        tuit.setStudent(std);
        paymentReminder.setTuition(tuit);
        when(paymentRepository.findById(888L)).thenReturn(Optional.of(paymentReminder));
        ValidationException ex = assertThrows(ValidationException.class, () -> paymentService.sendPaymentReminder(888L));
        assertTrue(ex.getMessage().contains("Reminder already sent for payment id : 888"));
    }

    @Test
    void testDeletePayments() {
        List<Long> ids = Arrays.asList(11L, 22L, 33L);
        paymentService.deletePayments(ids);
        verify(paymentRepository).deleteAllById(ids);
    }

    @Test
    void testGetPaymentInvoice_success() {
        Payment paymentInvoice = new Payment();
        paymentInvoice.setInvoiceUrl("invoice_test.pdf");
        when(paymentRepository.findById(555L)).thenReturn(Optional.of(paymentInvoice));
        byte[] dummyData = "dummy".getBytes();
        when(invoiceService.downloadInvoiceByFileName("invoice_test.pdf")).thenReturn(dummyData);
        byte[] result = paymentService.getPaymentInvoice(555L);
        assertArrayEquals(dummyData, result);
    }

    @Test
    void testGetPaymentInvoice_notFound() {
        when(paymentRepository.findById(666L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class, () -> paymentService.getPaymentInvoice(666L));
        assertTrue(ex.getMessage().contains("No payment fort id : 666"));
    }
}

package com.tuneup.tuneup.payments.repository;

import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatus(String status);

    @Query("SELECT new com.tuneup.tuneup.payments.PaymentDto( " +
            "p.id, t.id, l.availability.startTime,l.id, p.amount, p.status, " +
            "sp.displayName, p.dueDate, p.invoiceUrl, p.paidOn, p.reminderSentOn) " +
            "FROM Payment p " +
            "JOIN p.tuition t " +
            "JOIN t.student sp " +
            "JOIN p.lesson l " +
            "JOIN l.availability a " +
            "WHERE t.tutor.id = :tutorId " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:profileId IS NULL OR sp.id = :profileId)")
    Page<PaymentDto> findAllByTutorId(@Param("tutorId") long tutorId,
                                      @Param("status") PaymentStatus status,
                                      @Param("profileId") Long profileId,
                                      Pageable pageable);



    @Query("SELECT new com.tuneup.tuneup.payments.PaymentDto( " +
            "p.id, t.id, l.availability.startTime,l.id, p.amount, p.status, " +
            "tp.displayName, p.dueDate, p.invoiceUrl, p.paidOn, p.reminderSentOn) " +
            "FROM Payment p " +
            "JOIN p.tuition t " +
            "JOIN t.tutor tp " +
            "JOIN p.lesson l " +
            "JOIN l.availability a " +
            "WHERE t.student.id = :studentId " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:profileId IS NULL OR tp.id = :profileId)")
    Page<PaymentDto> findAllByStudentId(@Param("studentId") long studentId,
                                        @Param("status") PaymentStatus status,
                                        @Param("profileId") Long profileId,
                                        Pageable pageable);


    Boolean existsByLessonId(Long lessonId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'DUE' AND p.dueDate < :today")
    List<Payment> findDuePaymentsPastDueDate(@Param("today") LocalDate today);

}





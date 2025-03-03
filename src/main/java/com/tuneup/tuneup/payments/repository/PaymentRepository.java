package com.tuneup.tuneup.payments.repository;

import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(String status);


    @Query("SELECT new com.tuneup.tuneup.payments.PaymentDto( " +
                "p.id, t.id, p.lessonDate, p.amount, p.status, " +
                "sp.displayName, p.dueDate, p.invoiceUrl) " +
                "FROM Payment p " +
                "JOIN p.tuition t " +
                "JOIN t.student sp " +
                "WHERE t.tutor.id = :tutorId")
    List<PaymentDto> findAllByTutorId(@Param("tutorId") long tutorId);


    @Query("SELECT new com.tuneup.tuneup.payments.PaymentDto( " +
                "p.id, t.id, p.lessonDate, p.amount, p.status, " +
                "tp.displayName, p.dueDate, p.invoiceUrl) " +
                "FROM Payment p " +
                "JOIN p.tuition t " +
                "JOIN t.tutor tp " +
                "WHERE t.student.id = :studentId")
        List<PaymentDto> findAllByStudentId(@Param("studentId") long studentId);

}


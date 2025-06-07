package com.app.playbooker.repository;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    Page<Booking> findAllByUserId(String userId, Pageable pageable);
    Booking findByPaymentId(String paymentId);
    Page<Booking> findAllByPlaySpaceId(String id, Pageable pageable);
    List<Booking> findAllByBookingStatus(BookingStatus bookingStatus);
    List<Booking> findByPlaySpaceIdAndBookingDateAndBookingStatusIn(String playSpaceId, LocalDate date, List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN :start AND :end AND b.bookingStatus = 'CONFIRMED' AND b.reminderSent = false")
    List<Booking> findConfirmedBookingsWithinWindow(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    Long countByBookingStatus(BookingStatus bookingStatus);
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.paymentStatus = :status")
    BigDecimal sumAmountByPaymentStatus(@Param("status") PaymentStatus status);

}

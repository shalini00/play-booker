package com.app.playbooker.repository;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    Page<Booking> findAllByUserId(String userId, Pageable pageable);
    Booking findByPaymentId(String paymentId);
    Page<Booking> findAllByPlaySpaceId(String id, Pageable pageable);
    List<Booking> findAllByBookingStatus(BookingStatus bookingStatus);
    List<Booking> findByPlaySpaceIdAndBookingDateAndBookingStatusIn(String playSpaceId, LocalDate date, List<BookingStatus> statuses);
}

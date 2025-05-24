package com.app.playbooker.repository;

import com.app.playbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findAllByUserId(String userId);
    Booking findByPaymentId(String paymentId);
}

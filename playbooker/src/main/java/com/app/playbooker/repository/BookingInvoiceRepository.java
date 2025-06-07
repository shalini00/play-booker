package com.app.playbooker.repository;

import com.app.playbooker.entity.BookingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingInvoiceRepository extends JpaRepository<BookingInvoice, String> {
    Optional<BookingInvoice> findByBookingId(String id);

}

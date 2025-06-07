package com.app.playbooker.cronjobs;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import com.app.playbooker.repository.BookingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@Log4j2
public class UpdateBookingJob {

    @Autowired
    BookingRepository bookingRepository;

    @Scheduled(fixedDelayString = "300000")
    public void updatePendingPayment() {
        // At the start of the scheduled task
        log.info("Job triggered for updating booking status");
        List<Booking> bookings = bookingRepository.findAllByBookingStatus(BookingStatus.PENDING);
        bookings.stream()
                .filter(booking -> booking.getUpdatedAt().plusMinutes(15).isBefore(LocalDateTime.now()))
                .forEach(booking -> {
                    log.info("Updating booking with id : {}", booking.getId());
                    booking.setBookingStatus(BookingStatus.FAILED);
                    booking.setPaymentStatus(PaymentStatus.FAILED);
                    booking.setBookingFailedReason("Unable to verify payment");
                    bookingRepository.save(booking);
                });

    }
}

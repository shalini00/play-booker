package com.app.playbooker.cronjobs;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.app.playbooker.notification.NotificationFactory;
import com.app.playbooker.notification.NotificationService;
import com.app.playbooker.repository.BookingRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.service.PlaySpaceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.app.playbooker.service.BookingService.EMAIL;
import static com.app.playbooker.service.BookingService.WHATSAPP;

@Component
@Log4j2
public class BookingReminderJob {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private NotificationFactory notificationFactory;

    @Scheduled(fixedRate = 120000) // Every 15 minutes
    public void sendReminders() {
        log.info("Executing Job for Sending Reminders");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1).truncatedTo(ChronoUnit.MINUTES);

        List<Booking> upcomingBookings = bookingRepository
                .findConfirmedBookingsWithinWindow(start, end);

        for (Booking booking : upcomingBookings) {
            log.info("Sending reminders for booking : {}", booking.getId());
            User user = userRepository.findById(booking.getUserId()).get();
            PlaySpace playSpace = playSpaceService.getPlaySpaceObjectById(booking.getPlaySpaceId());
            NotificationService notificationService = StringUtils.hasText(user.getPhoneNumber()) && user.isPhoneNumberVerified()
                    ? notificationFactory.getNotificationServiceByType(WHATSAPP)
                    : notificationFactory.getNotificationServiceByType(EMAIL);

            notificationService.sendBookingReminder(user, playSpace, booking);
            booking.setReminderSent(true);
            bookingRepository.save(booking);
        }
    }
}

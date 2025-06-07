package com.app.playbooker.notification;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import freemarker.template.Configuration;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service(value = "whatsapp-notification")
@Log4j2
public class WhatsappNotificationService implements NotificationService{

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    @Autowired
    private Configuration freemarkerConfig;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Async
    @Override
    public void sendBookingConfirmation(User user, PlaySpace playSpace, Booking booking, ByteArrayOutputStream pdfStream) {
        String start = booking.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String end = booking.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String bookingTime = String.format("From - %s | To - %s", start, end);

        String msg = "Hi %s,\n" +
                "Your booking at %s is confirmed.\n" +
                "Booking Date: %s\n" +
                "Booking Time: %s\n\n" +
                "Thanks for using PlayBooker!";
        String finalMsg = String.format(msg, user.getName(), playSpace.getName(), booking.getBookingDate(), bookingTime);
        sendWhatsappMessage(finalMsg, user);
    }

    @Async
    @Override
    public void sendBookingCancellation(User user, PlaySpace playSpace, Booking booking) {
        String start = booking.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String end = booking.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String bookingTime = String.format("From - %s | To - %s", start, end);

        String msg = "Hi %s,\n" +
                "Your booking at %s was cancelled.\n" +
                "Booking Date: %s\n" +
                "Booking Time: %s\n\n" +
                "Thanks for using PlayBooker!";
        String finalMsg = String.format(msg, user.getName(), playSpace.getName(), booking.getBookingDate(), bookingTime);
        sendWhatsappMessage(finalMsg, user);
    }

    @Override
    public void sendBookingReminder(User user, PlaySpace playSpace, Booking booking) {
        String msg = String.format("Hi %s, this is a reminder for your booking at %s at %s.",
                user.getName(), playSpace.getName(), booking.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
        sendWhatsappMessage(msg, user);
    }

    @Override
    public void sendOTP(User user, String otp) {
        String msg = String.format("Hi %s.\nYour OTP code is: %s.\nThis OTP will expire in 5 minutes.",
                user.getName(), otp
        );
        sendWhatsappMessage(msg, user);
    }

    public void sendWhatsappMessage(String msg, User user) {
        try {
            Message.creator(
                    new PhoneNumber("whatsapp:" + user.getPhoneNumber()),
                    new PhoneNumber(fromNumber),
                    msg
            ).create();
            log.info("Whatsapp message sent to {}", user.getPhoneNumber());
        } catch (Exception e) {
            log.error("Error sending whatsapp message to {}: {}", user.getPhoneNumber(), e.getMessage());
        }
    }

}

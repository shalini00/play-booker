package com.app.playbooker.notification;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.itextpdf.io.source.ByteArrayOutputStream;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service(value = "email-notification")
@Log4j2
public class EmailNotificationService implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration freemarkerConfig;

    @Retryable(
            retryFor = { MessagingException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Async
    @Override
    public void sendBookingConfirmation(User user, PlaySpace playSpace, Booking booking, ByteArrayOutputStream pdfStream) {
        log.info("Executing send booking confirmation on thread :: {}", Thread.currentThread().getName());
        String subject = "Booking Confirmed - PlayBooker";
        String msg = getMsgFromFtl("booking-confirmation.ftl", user, playSpace, booking);
        sendEmail(subject,  msg, user, pdfStream);
    }

    @Async
    @Override
    public void sendBookingCancellation(User user, PlaySpace playSpace, Booking booking) {
        String subject = "Booking Cancelled - PlayBooker";
        String msg = getMsgFromFtl("booking-cancellation.ftl", user, playSpace, booking);
        sendEmail(subject, msg, user, null);
    }

    @Override
    public void sendBookingReminder(User user, PlaySpace playSpace, Booking booking) {
        String msg = String.format("Hi %s, this is a reminder for your booking at %s at %s.",
                user.getName(), playSpace.getName(), booking.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
        String subject = "Booking Reminder - PlayBooker";
        sendEmail(subject, msg, user, null);
    }

    @Override
    public void sendOTP(User user, String otp) {
        String msg = String.format("Hi %s.\nYour OTP code is: %s.\nThis OTP will expire in 5 minutes.",
                user.getName(), otp
        );
        String subject = "Email Verification - PlayBooker";
        sendEmail(subject, msg, user, null);
    }

    public void sendEmail(String emailSubject, String msg, User user, ByteArrayOutputStream pdfStream) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject(emailSubject);
            helper.setText(msg, true);

            // Attach PDF only if it's present
            if (pdfStream != null) {
                ByteArrayResource pdfAttachment = new ByteArrayResource(pdfStream.toByteArray());
                helper.addAttachment("Invoice.pdf", pdfAttachment);
            }

            javaMailSender.send(message);
            log.info("Email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    public String getMsgFromFtl(String fileName, User user, PlaySpace playSpace, Booking booking) {
        String start = booking.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String end = booking.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String bookingTime = String.format("From - %s | To - %s", start, end);
        // Prepare model
        Map<String, Object> model = new HashMap<>();
        model.put("userName", user.getName());
        model.put("playSpaceName", playSpace.getName());
        model.put("bookingTime", bookingTime);
        model.put("bookingDate", booking.getBookingDate());

        try {
            Template template = freemarkerConfig.getTemplate(fileName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}


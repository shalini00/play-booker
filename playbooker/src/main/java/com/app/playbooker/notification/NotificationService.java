package com.app.playbooker.notification;

import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.User;
import com.itextpdf.io.source.ByteArrayOutputStream;

public interface NotificationService {
    void sendBookingConfirmation(User user, PlaySpace playSpace, Booking booking, ByteArrayOutputStream pdfStream);
    void sendBookingCancellation(User user, PlaySpace playSpace, Booking booking);
    void sendBookingReminder(User user, PlaySpace playSpace, Booking booking);
    void sendOTP(User user, String otp);
}

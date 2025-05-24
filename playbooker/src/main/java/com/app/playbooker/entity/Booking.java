package com.app.playbooker.entity;

import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;
    private String playSpaceId;
    private LocalDate bookingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private Double totalPrice;

    // - Payment
    private String paymentId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String paymentReceiptId;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    private String bookingFailedReason;
}

package com.app.playbooker.entity;

import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "User Id is mandatory")
    private String userId;

    @NotBlank(message = "PlaySpace Id is mandatory")
    private String playSpaceId;

    @NotNull(message = "Booking Date is mandatory")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is mandatory")
    private LocalDateTime startTime;

    @NotNull(message = "End time Id is mandatory")
    private LocalDateTime endTime;

    private Double totalPrice;

    // - Payment
    private String paymentId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String paymentReceiptId;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    private String bookingFailedReason;

    private boolean reminderSent = false;
}

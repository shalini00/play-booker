package com.app.playbooker.dto;

import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BookingResponse {
    private String id;
    private String userId;
    private String email;
    private String playSpaceId;
    private LocalDate bookingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Double totalPrice;
    private PaymentStatus paymentStatus;
    private String paymentId;
    private String paymentReceiptId;
    private BookingStatus bookingStatus;
}

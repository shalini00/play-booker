package com.app.playbooker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String invoiceNumber;
    private String bookingId;
    private String userName;
    private String playSpaceName;
    private String playSpaceId;
    private Double amount;
    private LocalDateTime generatedAt;
    private boolean sent;
}

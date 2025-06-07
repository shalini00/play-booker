package com.app.playbooker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    @NotBlank(message = "PlaySpace Id is mandatory")
    private String playSpaceId;

    @NotNull(message = "Booking Date is mandatory")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is mandatory")
    private LocalDateTime startTime;

    @NotNull(message = "End time is mandatory")
    private LocalDateTime endTime;
}

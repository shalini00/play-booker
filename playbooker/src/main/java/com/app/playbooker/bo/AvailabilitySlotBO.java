package com.app.playbooker.bo;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AvailabilitySlotBO {

    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked;
}

package com.app.playbooker.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SlotInfoDTO {
    private LocalTime time;
    private boolean available;
}

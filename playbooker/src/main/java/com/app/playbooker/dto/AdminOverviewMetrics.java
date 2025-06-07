package com.app.playbooker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminOverviewMetrics {
    private long totalBookings;
    private long activePlaySpaces;
    private BigDecimal totalRevenue;
    private long newUserSignups;
}

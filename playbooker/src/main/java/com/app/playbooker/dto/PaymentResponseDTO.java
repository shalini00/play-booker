package com.app.playbooker.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaymentResponseDTO {
    private String id;
    private String status;
    private String receipt;
}

package com.app.playbooker.models;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class Address {
    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
    private Double latitude;
    private Double longitude;
}

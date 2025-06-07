package com.app.playbooker.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class Address {
    @NotBlank(message = "Address line is mandatory")
    private String addressLine;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "Pin code is mandatory")
    private String pinCode;

    @NotNull(message = "Latitude is mandatory")
    private Double latitude;

    @NotNull(message = "Longitude name is mandatory")
    private Double longitude;
}

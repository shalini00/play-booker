package com.app.playbooker.dto;

import com.app.playbooker.enums.PlaySpaceVisibility;
import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.models.OpeningHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlaySpaceDTO {

    @NotBlank(message = "PlaySpace name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Price per hour is mandatory")
    private Double pricePerHour;

    @Valid
    private Address address;

    private List<Sport> sports;
    private List<String> amenities;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer numberOfReviews;
    private List<OpeningHours> weeklyOpeningHours;
    private PlaySpaceVisibility playSpaceVisibility;
}

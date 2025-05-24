package com.app.playbooker.dto;

import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.entity.AvailabilitySlot;
import com.app.playbooker.models.OpeningHours;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlaySpaceDTO {
    private String name;
    private String description;
    private Address address;
    private Sport sport;
    private Double pricePerHour;
    private List<String> amenities;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer numberOfReviews;
    private List<OpeningHours> weeklyOpeningHours;
    private List<AvailabilitySlot> availabilitySlots;
}

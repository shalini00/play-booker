package com.app.playbooker.dto;

import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.models.OpeningHours;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePlaySpaceDTO {
    private String name;
    private String description;
    private List<Sport> sports;
    private Double pricePerHour;
    private List<String> amenities;
    private List<String> imageUrls;
    private List<OpeningHours> weeklyOpeningHours;
    private Address address;
}

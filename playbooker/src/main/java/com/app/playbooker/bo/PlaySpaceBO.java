package com.app.playbooker.bo;

import com.app.playbooker.enums.PlaySpaceVisibility;
import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.models.OpeningHours;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlaySpaceBO {
    private String id;
    private String name;
    private String description;
    private Address address;
    private List<Sport> sports;
    private Double pricePerHour;
    private List<String> amenities;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer numberOfReviews;
    private List<OpeningHours> weeklyOpeningHours;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private PlaySpaceVisibility playSpaceVisibility;
}

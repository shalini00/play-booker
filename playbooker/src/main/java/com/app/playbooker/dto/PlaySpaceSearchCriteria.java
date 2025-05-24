package com.app.playbooker.dto;

import com.app.playbooker.enums.Sport;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlaySpaceSearchCriteria {
    private String name;
    private Sport sport;
    private String city;
    private Double averageRating;
}

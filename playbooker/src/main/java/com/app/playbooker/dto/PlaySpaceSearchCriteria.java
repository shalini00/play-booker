package com.app.playbooker.dto;

import com.app.playbooker.enums.Sport;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PlaySpaceSearchCriteria {
    private String name;
    private List<Sport> sports;
    private String city;
    private Double averageRating;
    private Sort sortOrders;
}

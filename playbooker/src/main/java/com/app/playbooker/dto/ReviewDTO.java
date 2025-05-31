package com.app.playbooker.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    public String id;
    private String playSpaceId;
    private double rating;
    private String comment;
}

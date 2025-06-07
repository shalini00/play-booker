package com.app.playbooker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {

    @NotBlank(message = "PlaySpace Id is mandatory")
    private String playSpaceId;

    @NotNull(message = "Rating is mandatory")
    private double rating;

    private String comment;
}

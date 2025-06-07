package com.app.playbooker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "PlaySpace Id is mandatory")
    private String playSpaceId;

    @NotBlank(message = "User Id is mandatory")
    private String userId;

    @NotNull(message = "Rating is mandatory")
    private double rating;

    private String comment;
    private LocalDateTime createdAt;
}

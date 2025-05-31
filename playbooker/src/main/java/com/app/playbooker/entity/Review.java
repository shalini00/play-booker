package com.app.playbooker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String playSpaceId;
    private String userId;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;
}

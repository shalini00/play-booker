package com.app.playbooker.entity;

import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.models.OpeningHours;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlaySpace extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Embedded
    private Address address;

    @ElementCollection(targetClass = Sport.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "playspace_sports", joinColumns = @JoinColumn(name = "playspace_id"))
    @Column(name = "sports")
    private List<Sport> sports;

    @Column(nullable = false)
    private Double pricePerHour;

    @ElementCollection
    private List<String> amenities;

    @ElementCollection
    private List<String> imageUrls;

    @ElementCollection
    private List<OpeningHours> weeklyOpeningHours;

    private Double averageRating;
    private Integer numberOfReviews;
}

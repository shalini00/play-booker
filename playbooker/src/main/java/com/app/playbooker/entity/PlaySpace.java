package com.app.playbooker.entity;

import com.app.playbooker.enums.PlaySpaceVisibility;
import com.app.playbooker.enums.Sport;
import com.app.playbooker.models.Address;
import com.app.playbooker.models.OpeningHours;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false)
    @NotBlank(message = "PlaySpace name is mandatory")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Description is mandatory")
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
    @NotNull(message = "Price per hour is mandatory")
    private Double pricePerHour;

    @ElementCollection
    private List<String> amenities;

    @ElementCollection
    private List<String> imageUrls;

    @ElementCollection
    private List<OpeningHours> weeklyOpeningHours;

    private Double averageRating;
    private Integer numberOfReviews;

    @Enumerated(EnumType.STRING)
    private PlaySpaceVisibility playSpaceVisibility;
}

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
public class PlaySpace {
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

    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Sport sport;

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

    @OneToMany(mappedBy = "playSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> availabilitySlots;

}

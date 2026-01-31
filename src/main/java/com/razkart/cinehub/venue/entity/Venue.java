package com.razkart.cinehub.venue.entity;

import com.razkart.cinehub.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Venue entity representing theaters/multiplexes.
 */
@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 200)
    private String landmark;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(columnDefinition = "JSON")
    private String facilities;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VenueStatus status = VenueStatus.ACTIVE;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Screen> screens = new ArrayList<>();

    public void addScreen(Screen screen) {
        screens.add(screen);
        screen.setVenue(this);
    }

    public void removeScreen(Screen screen) {
        screens.remove(screen);
        screen.setVenue(null);
    }

    public boolean isActive() {
        return this.status == VenueStatus.ACTIVE;
    }
}

package com.razkart.cinehub.venue.repository;

import com.razkart.cinehub.venue.entity.Screen;
import com.razkart.cinehub.venue.entity.ScreenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByVenueIdAndIsActiveTrue(Long venueId);

    List<Screen> findByVenueId(Long venueId);

    List<Screen> findByVenueIdAndScreenType(Long venueId, ScreenType screenType);

    boolean existsByVenueIdAndName(Long venueId, String name);
}

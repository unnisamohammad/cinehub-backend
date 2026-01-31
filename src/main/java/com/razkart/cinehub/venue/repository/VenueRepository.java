package com.razkart.cinehub.venue.repository;

import com.razkart.cinehub.venue.entity.Venue;
import com.razkart.cinehub.venue.entity.VenueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByCityIdAndStatus(Long cityId, VenueStatus status);

    Page<Venue> findByCityId(Long cityId, Pageable pageable);

    @Query("SELECT v FROM Venue v WHERE v.city.id = :cityId AND v.status = :status")
    List<Venue> findActiveVenuesByCity(@Param("cityId") Long cityId, @Param("status") VenueStatus status);

    @Query("SELECT v FROM Venue v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Venue> searchVenues(@Param("query") String query, Pageable pageable);

    @Query("SELECT v FROM Venue v JOIN v.screens s JOIN com.razkart.cinehub.show.entity.Show sh ON sh.screen.id = s.id " +
           "WHERE sh.event.id = :eventId AND v.city.id = :cityId AND v.status = 'ACTIVE'")
    List<Venue> findVenuesShowingEvent(@Param("eventId") Long eventId, @Param("cityId") Long cityId);

    boolean existsByNameAndCityId(String name, Long cityId);
}

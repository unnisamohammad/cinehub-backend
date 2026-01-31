package com.razkart.cinehub.show.repository;

import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.show.entity.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("SELECT s FROM Show s WHERE s.event.id = :eventId AND s.showDate = :date AND s.status = :status " +
           "ORDER BY s.startTime")
    List<Show> findByEventIdAndDateAndStatus(
            @Param("eventId") Long eventId,
            @Param("date") LocalDate date,
            @Param("status") ShowStatus status);

    @Query("SELECT s FROM Show s WHERE s.event.id = :eventId AND s.screen.venue.city.id = :cityId " +
           "AND s.showDate = :date AND s.status = 'SCHEDULED' ORDER BY s.startTime")
    List<Show> findByEventAndCityAndDate(
            @Param("eventId") Long eventId,
            @Param("cityId") Long cityId,
            @Param("date") LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.screen.venue.id = :venueId AND s.showDate = :date " +
           "AND s.status = 'SCHEDULED' ORDER BY s.startTime")
    List<Show> findByVenueAndDate(@Param("venueId") Long venueId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.screen.id = :screenId AND s.showDate = :date " +
           "ORDER BY s.startTime")
    List<Show> findByScreenAndDate(@Param("screenId") Long screenId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.event.id = :eventId AND s.showDate >= :fromDate " +
           "AND s.status = 'SCHEDULED' ORDER BY s.showDate, s.startTime")
    List<Show> findUpcomingByEvent(@Param("eventId") Long eventId, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT DISTINCT s.showDate FROM Show s WHERE s.event.id = :eventId " +
           "AND s.screen.venue.city.id = :cityId AND s.showDate >= :fromDate " +
           "AND s.status = 'SCHEDULED' ORDER BY s.showDate")
    List<LocalDate> findAvailableDatesByEventAndCity(
            @Param("eventId") Long eventId,
            @Param("cityId") Long cityId,
            @Param("fromDate") LocalDate fromDate);

    boolean existsByScreenIdAndShowDateAndStartTime(Long screenId, LocalDate showDate, LocalTime startTime);
}

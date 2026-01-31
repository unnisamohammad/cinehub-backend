package com.razkart.cinehub.event.repository;

import com.razkart.cinehub.event.entity.Event;
import com.razkart.cinehub.event.entity.EventCategory;
import com.razkart.cinehub.event.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCategory(EventCategory category);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByCategoryAndStatus(EventCategory category, EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.releaseDate <= :date ORDER BY e.releaseDate DESC")
    List<Event> findNowShowing(@Param("status") EventStatus status, @Param("date") LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.releaseDate > :date ORDER BY e.releaseDate ASC")
    List<Event> findComingSoon(@Param("status") EventStatus status, @Param("date") LocalDate date);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.genre) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Event> searchEvents(@Param("query") String query, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.language = :language AND e.status = :status")
    List<Event> findByLanguageAndStatus(@Param("language") String language, @Param("status") EventStatus status);

    boolean existsByTitle(String title);
}

package com.razkart.cinehub.venue.repository;

import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScreenIdOrderByRowNameAscSeatNumberAsc(Long screenId);

    List<Seat> findByScreenId(Long screenId);

    List<Seat> findByScreenIdAndSeatType(Long screenId, SeatType seatType);

    List<Seat> findByScreenIdAndIsAvailableTrue(Long screenId);

    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.rowName = :rowName ORDER BY s.seatNumber")
    List<Seat> findByScreenIdAndRowName(@Param("screenId") Long screenId, @Param("rowName") String rowName);

    @Query("SELECT DISTINCT s.rowName FROM Seat s WHERE s.screen.id = :screenId ORDER BY s.rowName")
    List<String> findDistinctRowsByScreenId(@Param("screenId") Long screenId);

    int countByScreenId(Long screenId);
}

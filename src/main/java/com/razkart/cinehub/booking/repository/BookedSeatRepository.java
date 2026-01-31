package com.razkart.cinehub.booking.repository;

import com.razkart.cinehub.booking.entity.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    List<BookedSeat> findByBookingId(Long bookingId);

    @Query("SELECT bs.seat.id FROM BookedSeat bs WHERE bs.show.id = :showId " +
           "AND bs.booking.status IN ('PENDING', 'CONFIRMED')")
    Set<Long> findBookedSeatIdsByShowId(@Param("showId") Long showId);

    @Query("SELECT COUNT(bs) FROM BookedSeat bs WHERE bs.show.id = :showId " +
           "AND bs.booking.status IN ('PENDING', 'CONFIRMED')")
    long countBookedSeatsForShow(@Param("showId") Long showId);

    boolean existsByShowIdAndSeatId(Long showId, Long seatId);
}

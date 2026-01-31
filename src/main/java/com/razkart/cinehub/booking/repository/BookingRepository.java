package com.razkart.cinehub.booking.repository;

import com.razkart.cinehub.booking.entity.Booking;
import com.razkart.cinehub.booking.entity.BookingStatus;
import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingNumber(String bookingNumber);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByShowId(Long showId);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.show = :show AND b.status IN :statuses")
    List<Booking> findByUserAndShowAndStatusIn(
            @Param("user") User user,
            @Param("show") Show show,
            @Param("statuses") List<BookingStatus> statuses);

    default boolean existsByUserAndShowAndStatusIn(User user, Show show, List<BookingStatus> statuses) {
        return !findByUserAndShowAndStatusIn(user, show, statuses).isEmpty();
    }

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.expiresAt < :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.show.id = :showId AND b.status IN ('PENDING', 'CONFIRMED')")
    long countActiveBookingsForShow(@Param("showId") Long showId);
}

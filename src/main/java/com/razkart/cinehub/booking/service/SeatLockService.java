package com.razkart.cinehub.booking.service;

import java.util.List;
import java.util.Set;

/**
 * Service interface for Redis-based seat locking.
 */
public interface SeatLockService {

    /**
     * Lock seats for a user. Returns list of successfully locked seat IDs.
     */
    List<Long> lockSeats(Long showId, List<Long> seatIds, Long userId, int expiryMinutes);

    /**
     * Release specific seats.
     */
    void releaseSeats(Long showId, List<Long> seatIds);

    /**
     * Release all seats locked by a user for a show.
     */
    void releaseSeatsByUser(Long showId, Long userId);

    /**
     * Get all locked seat IDs for a show.
     */
    Set<Long> getLockedSeats(Long showId);

    /**
     * Check if a specific seat is available (not locked).
     */
    boolean isSeatAvailable(Long showId, Long seatId);
}

package com.razkart.cinehub.show.repository;

import com.razkart.cinehub.show.entity.ShowPricing;
import com.razkart.cinehub.venue.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowPricingRepository extends JpaRepository<ShowPricing, Long> {

    List<ShowPricing> findByShowId(Long showId);

    Optional<ShowPricing> findByShowIdAndSeatType(Long showId, SeatType seatType);

    void deleteByShowId(Long showId);
}

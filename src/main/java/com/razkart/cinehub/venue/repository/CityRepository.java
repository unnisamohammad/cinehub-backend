package com.razkart.cinehub.venue.repository;

import com.razkart.cinehub.venue.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<City> findByNameAndState(String name, String state);

    List<City> findByCountry(String country);

    boolean existsByNameAndState(String name, String state);
}

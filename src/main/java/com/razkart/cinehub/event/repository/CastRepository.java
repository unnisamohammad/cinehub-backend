package com.razkart.cinehub.event.repository;

import com.razkart.cinehub.event.entity.Cast;
import com.razkart.cinehub.event.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {

    List<Cast> findByEventIdOrderByDisplayOrderAsc(Long eventId);

    List<Cast> findByEventIdAndRoleType(Long eventId, RoleType roleType);

    void deleteByEventId(Long eventId);
}

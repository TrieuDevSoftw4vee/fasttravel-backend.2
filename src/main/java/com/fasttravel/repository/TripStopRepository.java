package com.fasttravel.repository;

import com.fasttravel.entity.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripStopRepository extends JpaRepository<TripStop, Long> {
    List<TripStop> findByTripIdOrderByStopOrderAsc(Long tripId);
}
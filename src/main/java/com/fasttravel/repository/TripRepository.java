package com.fasttravel.repository;

import com.fasttravel.entity.Trip;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("select t from Trip t join fetch t.route r join fetch r.originStation o join fetch r.destinationStation d join fetch t.vehicle v where t.status=:status and t.departureTime >= :from and t.departureTime < :to and (:originProvince is null or o.province.id=:originProvince) and (:destinationProvince is null or d.province.id=:destinationProvince) order by t.departureTime")
    List<Trip> search(@Param("originProvince") Long originProvince, @Param("destinationProvince") Long destinationProvince, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("status") Trip.Status status);
}

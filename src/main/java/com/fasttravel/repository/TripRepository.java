package com.fasttravel.repository;

import com.fasttravel.entity.Trip;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("select t from Trip t join fetch t.route r join fetch r.originStation o join fetch r.destinationStation d join fetch t.vehicle v where t.status=:status and t.departureTime >= :from and t.departureTime < :to and (:originProvince is null or o.province.id=:originProvince) and (:destinationProvince is null or d.province.id=:destinationProvince) order by t.departureTime")
    List<Trip> search(@Param("originProvince") Long originProvince, @Param("destinationProvince") Long destinationProvince, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("status") Trip.Status status);
    @Query("SELECT t FROM Trip t WHERE t.route.id = :routeId AND CAST(t.departureTime AS LocalDate) = :departureDate AND t.status = 'SCHEDULED'")
    List<Trip> searchTripsByRouteAndDate(@Param("routeId") Long routeId, @Param("departureDate") LocalDate departureDate);
    @Query("SELECT t FROM Trip t WHERE t.route.originStation.id = :originId " +
            "AND t.route.destinationStation.id = :destinationId " +
            "AND t.departureTime >= :startOfDay AND t.departureTime <= :endOfDay " +
            "AND t.status = 'SCHEDULED'")
    List<Trip> searchTrips(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}


package com.fasttravel.dao;

import com.fasttravel.entity.Trip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TripDAO extends BaseDAO<Trip, Long> {

    List<Trip> search(Long origin, Long destination, LocalDate date);

    @Query("SELECT new map(" +
            "t.id as tripId, " +
            "t.code as tripCode, " +
            "t.route.name as routeName, " +
            "t.departureTime as departureTime, " +
            "(SELECT COUNT(s) FROM TripSeat s WHERE s.trip.id = t.id) as totalSeats, " +
            "(SELECT COUNT(s) FROM TripSeat s WHERE s.trip.id = t.id AND s.status = 'BOOKED') as bookedSeats) " +
            "FROM Trip t " +
            "WHERE t.departureTime >= :startDate AND t.departureTime <= :endDate")
    List<Map<String, Object>> getOccupancyRate(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}
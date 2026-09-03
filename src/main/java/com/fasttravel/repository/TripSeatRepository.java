package com.fasttravel.repository;

import com.fasttravel.entity.TripSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface TripSeatRepository extends JpaRepository<TripSeat, Long> {
    List<TripSeat> findByTripIdOrderBySeatFloorAscSeatRowIndexAscSeatColumnIndexAsc(Long tripId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ts from TripSeat ts where ts.trip.id=:tripId and ts.id in :ids")
    List<TripSeat> lockSeats(@Param("tripId") Long tripId, @Param("ids") List<Long> ids);

    @Modifying
    @Query("update TripSeat ts set ts.status=:available,ts.holdToken=null,ts.holdExpiresAt=null where ts.status=:held and ts.holdExpiresAt<:now")
    int releaseExpired(@Param("now") LocalDateTime now, @Param("available") TripSeat.Status available, @Param("held") TripSeat.Status held);
    Optional<TripSeat> findByTripIdAndSeatId(Long tripId, Long seatId);
}

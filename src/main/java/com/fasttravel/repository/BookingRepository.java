package com.fasttravel.repository;

import com.fasttravel.entity.Booking;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDateTime;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByCode(String code);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByStatusAndExpiresAtBefore(Booking.Status status, LocalDateTime time);

    long countByStatus(Booking.Status status);
}

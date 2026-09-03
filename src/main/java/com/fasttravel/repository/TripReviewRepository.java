package com.fasttravel.repository;

import com.fasttravel.entity.TripReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripReviewRepository extends JpaRepository<TripReview, Long> {
    List<TripReview> findByTripIdOrderByCreatedAtDesc(Long tripId);
    boolean existsByBookingId(Long bookingId);
}
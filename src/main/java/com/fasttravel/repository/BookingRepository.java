package com.fasttravel.repository;

import com.fasttravel.entity.Booking;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByCode(String code);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByStatusAndExpiresAtBefore(Booking.Status status, LocalDateTime time);

    long countByStatus(Booking.Status status);

    // --- CÁC HÀM BỔ SUNG CHO DASHBOARD THỐNG KÊ ---

    @Query("SELECT new map(CAST(b.paidAt AS date) as date, SUM(b.totalAmount) as revenue) " +
            "FROM Booking b " +
            "WHERE b.status = 'PAID' AND b.paidAt >= :startDate AND b.paidAt <= :endDate " +
            "GROUP BY CAST(b.paidAt AS date) " +
            "ORDER BY CAST(b.paidAt AS date) ASC")
    List<Map<String, Object>> getRevenueByDate(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.status = 'PAID' AND b.paidAt BETWEEN :startDate AND :endDate")
    Long getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
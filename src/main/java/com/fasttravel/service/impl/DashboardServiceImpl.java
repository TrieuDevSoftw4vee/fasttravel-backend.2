package com.fasttravel.service.impl;

import com.fasttravel.dao.TripDAO;
import com.fasttravel.repository.BookingRepository;
import com.fasttravel.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BookingRepository bookings;
    private final TripDAO trips;

    @Override
    public Map<String, Object> getOverview(LocalDateTime start, LocalDateTime end) {
        Long totalRevenue = bookings.getTotalRevenue(start, end);

        List<Map<String, Object>> revenueChart = bookings.getRevenueByDate(start, end);

        List<Map<String, Object>> occupancyStats = trips.getOccupancyRate(start, end);

        // Tính toán tỷ lệ % lấp đầy
        occupancyStats.forEach(stat -> {
            long total = (long) stat.get("totalSeats");
            long booked = (long) stat.get("bookedSeats");
            double rate = total == 0 ? 0 : (double) booked / total * 100;
            stat.put("occupancyRate", Math.round(rate * 100.0) / 100.0);
        });

        return Map.of(
                "totalRevenue", totalRevenue != null ? totalRevenue : 0,
                "revenueChart", revenueChart,
                "occupancyChart", occupancyStats
        );
    }
}
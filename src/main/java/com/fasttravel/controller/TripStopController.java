package com.fasttravel.controller;

import com.fasttravel.dto.ApiResponse;
import com.fasttravel.entity.*;
import com.fasttravel.exception.AppException;
import com.fasttravel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/stops")
@RequiredArgsConstructor
public class TripStopController {

    private final TripStopRepository tripStopRepository;
    private final TripRepository tripRepository;
    private final StationRepository stationRepository;

    @GetMapping
    public ApiResponse<?> getStops(@PathVariable Long tripId) {
        return ApiResponse.ok(tripStopRepository.findByTripIdOrderByStopOrderAsc(tripId));
    }

    @PostMapping
    public ApiResponse<?> addStop(@PathVariable Long tripId,
                                  @RequestParam Long stationId,
                                  @RequestParam Integer order,
                                  @RequestParam String timeStr,
                                  @RequestParam TripStop.StopType type) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> AppException.notFound("Chuyến xe không tồn tại"));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> AppException.notFound("Trạm không tồn tại"));

        TripStop stop = new TripStop();
        stop.setTrip(trip);
        stop.setStation(station);
        stop.setStopOrder(order);
        stop.setEstimatedTime(LocalDateTime.parse(timeStr));
        stop.setStopType(type);

        return ApiResponse.ok("Thêm trạm dừng thành công", tripStopRepository.save(stop));
    }
}
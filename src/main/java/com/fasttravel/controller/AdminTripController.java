package com.fasttravel.controller;

import com.fasttravel.dao.TripDAO;
import com.fasttravel.dto.ApiResponse;
import com.fasttravel.dto.AdminTripDTO.SaveTripRequest; // Import DTO record lồng
import com.fasttravel.entity.*;
import com.fasttravel.exception.AppException;
import com.fasttravel.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/trips")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@RequiredArgsConstructor
public class AdminTripController {

    private final TripDAO tripDAO;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final TripSeatRepository tripSeatRepository;

    @PostMapping
    @Transactional
    public ApiResponse<?> createTrip(@Valid @RequestBody SaveTripRequest req) {
        Route route = routeRepository.findById(req.routeId())
                .orElseThrow(() -> new RuntimeException("Tuyến đường không tồn tại"));

        Vehicle vehicle = vehicleRepository.findById(req.vehicleId())
                .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));

        User driver = null;
        if (req.driverId() != null) {
            driver = userRepository.findById(req.driverId()).orElse(null);
        }

        Trip trip = new Trip();
        trip.setCode("TRIP" + System.currentTimeMillis());
        trip.setRoute(route);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setDepartureTime(req.departureTime());
        trip.setArrivalTime(req.arrivalTime());
        trip.setPrice(req.price());
        trip.setStatus(Trip.Status.SCHEDULED);

        Trip savedTrip = tripDAO.save(trip);

        // Sinh danh sách ghế TripSeat tự động cho chuyến đi
        List<Seat> seats = seatRepository.findByVehicleId(vehicle.getId());
        List<TripSeat> tripSeats = seats.stream().map(seat -> {
            TripSeat ts = new TripSeat();
            ts.setTrip(savedTrip);
            ts.setSeat(seat);
            ts.setStatus(TripSeat.Status.AVAILABLE);
            return ts;
        }).toList();
        tripSeatRepository.saveAll(tripSeats);

        return ApiResponse.ok("Tạo chuyến xe thành công", savedTrip.getId());
    }

    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<?> updateTrip(@PathVariable Long id, @Valid @RequestBody SaveTripRequest req) {
        Trip trip = tripDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Chuyến xe không tồn tại"));

        trip.setDepartureTime(req.departureTime());
        trip.setArrivalTime(req.arrivalTime());
        trip.setPrice(req.price());

        if (req.driverId() != null) {
            User driver = userRepository.findById(req.driverId()).orElse(null);
            trip.setDriver(driver);
        }

        tripDAO.save(trip);
        return ApiResponse.ok("Cập nhật chuyến xe thành công", trip.getId());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<?> deleteTrip(@PathVariable Long id) {
        Trip trip = tripDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Chuyến xe không tồn tại"));

        trip.setStatus(Trip.Status.CANCELLED);
        tripDAO.save(trip);
        return ApiResponse.ok("Đã hủy chuyến xe", id);
    }
}
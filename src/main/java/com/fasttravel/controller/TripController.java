package com.fasttravel.controller;

import com.fasttravel.entity.Trip;
import com.fasttravel.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@Tag(name = "Customer - Trip Search", description = "API tra cứu và lọc chuyến xe")
public class TripController {

    @Autowired
    private TripService tripService;

    @Operation(summary = "Tìm kiếm chuyến xe theo tuyến đường và ngày đi")
    @GetMapping("/search")
    public ResponseEntity<List<Trip>> searchTrips(
            @RequestParam Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {

        List<Trip> trips = tripService.searchTrips(routeId, departureDate);
        return ResponseEntity.ok(trips);
    }
}
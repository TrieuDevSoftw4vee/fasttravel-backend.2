package com.fasttravel.service.impl;

import com.fasttravel.dao.TripDAO;
import com.fasttravel.entity.Seat;
import com.fasttravel.entity.Station;
import com.fasttravel.entity.Trip;
import com.fasttravel.entity.TripSeat;
import com.fasttravel.exception.AppException;
import com.fasttravel.repository.StationRepository;
import com.fasttravel.repository.TripSeatRepository;
import com.fasttravel.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripDAO trips;
    private final StationRepository stations;
    private final TripSeatRepository tripSeats;

    /*
     * Chuyển thông tin bến xe thành dữ liệu trả về frontend.
     * LinkedHashMap cho phép giá trị null, Map.of thì không.
     */
    private Map<String, Object> station(Station station) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", station.getId());
        result.put("name", station.getName());

        result.put(
                "province",
                station.getProvince() != null
                        ? station.getProvince().getName()
                        : null
        );

        result.put("address", station.getAddress());

        return result;
    }

    /*
     * Chuyển thông tin chuyến xe thành dữ liệu trả về frontend.
     */
    private Map<String, Object> trip(Trip trip) {
        long availableSeats = tripSeats
                .findByTripIdOrderBySeatFloorAscSeatRowIndexAscSeatColumnIndexAsc(
                        trip.getId()
                )
                .stream()
                .filter(tripSeat ->
                        tripSeat.getStatus() == TripSeat.Status.AVAILABLE
                )
                .count();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", trip.getId());
        result.put("code", trip.getCode());
        result.put("routeName", trip.getRoute().getName());

        result.put(
                "origin",
                station(trip.getRoute().getOriginStation())
        );

        result.put(
                "destination",
                station(trip.getRoute().getDestinationStation())
        );

        result.put("departureTime", trip.getDepartureTime());
        result.put("arrivalTime", trip.getArrivalTime());
        result.put("price", trip.getPrice());

        Map<String, Object> vehicle = new LinkedHashMap<>();

        vehicle.put("id", trip.getVehicle().getId());
        vehicle.put("name", trip.getVehicle().getName());
        vehicle.put("type", trip.getVehicle().getType());
        vehicle.put("plate", trip.getVehicle().getLicensePlate());
        vehicle.put("floors", trip.getVehicle().getFloors());

        result.put("vehicle", vehicle);
        result.put("availableSeats", availableSeats);
        result.put("status", trip.getStatus());

        return result;
    }

    @Override
    public List<Map<String, Object>> stations() {
        return stations
                .findByActiveTrueOrderByProvinceNameAscNameAsc()
                .stream()
                .map(this::station)
                .toList();
    }

    @Override
    public List<Map<String, Object>> search(
            Long origin,
            Long destination,
            LocalDate date
    ) {
        return trips
                .search(origin, destination, date)
                .stream()
                .map(this::trip)
                .filter(item -> ((Number) item.get("availableSeats")).longValue() > 0)
                .toList();
    }

    @Override
    public Map<String, Object> detail(Long id) {
        Trip trip = trips.findById(id)
                .orElseThrow(() ->
                        AppException.notFound(
                                "Chuyến xe không tồn tại"
                        )
                );

        Map<String, Object> result =
                new LinkedHashMap<>(trip(trip));

        return result;
    }

    @Override
    public List<Map<String, Object>> seats(Long id) {
        trips.findById(id)
                .orElseThrow(() ->
                        AppException.notFound(
                                "Chuyến xe không tồn tại"
                        )
                );

        return tripSeats
                .findByTripIdOrderBySeatFloorAscSeatRowIndexAscSeatColumnIndexAsc(
                        id
                )
                .stream()
                .map(this::tripSeat)
                .toList();
    }

    private Map<String, Object> tripSeat(
            TripSeat tripSeat
    ) {
        Seat seat = tripSeat.getSeat();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", tripSeat.getId());
        result.put("number", seat.getSeatNumber());
        result.put("floor", seat.getFloor());
        result.put("row", seat.getRowIndex());
        result.put("column", seat.getColumnIndex());
        result.put("type", seat.getSeatType());
        result.put("status", tripSeat.getStatus());

        return result;
    }
}

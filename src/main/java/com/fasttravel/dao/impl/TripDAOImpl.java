package com.fasttravel.dao.impl;

import com.fasttravel.dao.TripDAO;
import com.fasttravel.entity.Station;
import com.fasttravel.entity.Trip;
import com.fasttravel.exception.AppException;
import com.fasttravel.repository.StationRepository;
import com.fasttravel.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TripDAOImpl implements TripDAO {
    private final TripRepository repo;
    private final StationRepository stations;

    public Trip save(Trip entity) { return repo.save(entity); }
    public Optional<Trip> findById(Long id) { return repo.findById(id); }
    public List<Trip> findAll() { return repo.findAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }

    public List<Trip> search(Long originStationId, Long destinationStationId, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = date == null ? now : date.atStartOfDay();
        LocalDateTime to = date == null ? now.plusYears(2) : date.plusDays(1).atStartOfDay();
        if (from.isBefore(now)) from = now;
        if (!to.isAfter(from)) return List.of();

        Long originProvinceId = provinceId(originStationId, "Điểm đi không tồn tại");
        Long destinationProvinceId = provinceId(destinationStationId, "Điểm đến không tồn tại");
        return repo.search(originProvinceId, destinationProvinceId, from, to, Trip.Status.SCHEDULED);
    }

    private Long provinceId(Long stationId, String message) {
        if (stationId == null) return null;
        Station station = stations.findById(stationId).orElseThrow(() -> AppException.bad(message));
        return station.getProvince().getId();
    }
}

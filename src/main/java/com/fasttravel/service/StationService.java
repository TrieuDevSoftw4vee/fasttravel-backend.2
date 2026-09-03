package com.fasttravel.service;

import com.fasttravel.entity.Station;
import com.fasttravel.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station getStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bến xe với ID: " + id));
    }

    public Station createStation(Station station) {
        if (stationRepository.existsByName(station.getName())) {
            throw new RuntimeException("Tên bến xe đã tồn tại trong hệ thống!");
        }
        return stationRepository.save(station);
    }

    public Station updateStation(Long id, Station stationDetails) {
        Station existingStation = getStationById(id);

        existingStation.setProvince(stationDetails.getProvince());
        existingStation.setName(stationDetails.getName());
        existingStation.setAddress(stationDetails.getAddress());
        existingStation.setLatitude(stationDetails.getLatitude());
        existingStation.setLongitude(stationDetails.getLongitude());
        existingStation.setPhone(stationDetails.getPhone());
        existingStation.setActive(stationDetails.isActive());

        return stationRepository.save(existingStation);
    }

    public void deleteStation(Long id) {
        Station existingStation = getStationById(id);
        stationRepository.delete(existingStation);
    }
}
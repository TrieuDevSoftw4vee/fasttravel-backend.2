package com.fasttravel.service.impl;

import com.fasttravel.entity.Vehicle;
import com.fasttravel.repository.VehicleRepository;
import com.fasttravel.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private static final String LICENSE_PLATE_REGEX = "^[0-9]{2}[A-Z][0-9]?-[0-9]{3}\\.[0-9]{2}$|^[0-9]{2}[A-Z][0-9]?-[0-9]{4,5}$";

    @Override
    public boolean isValidLicensePlate(String plate) {
        if (plate == null) return false;
        return Pattern.matches(LICENSE_PLATE_REGEX, plate.trim().toUpperCase());
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương tiện với ID: " + id));
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        if (!isValidLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("Biển số xe không hợp lệ! Định dạng chuẩn ví dụ: 51B-123.45 hoặc 70B-67890");
        }
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("Biển số xe này đã tồn tại trong hệ thống!");
        }
        if (vehicle.getTotalSeats() < 4 || vehicle.getTotalSeats() > 60) {
            throw new RuntimeException("Tổng số ghế phải nằm trong khoảng từ 4 đến 60!");
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle existingVehicle = getVehicleById(id);

        if (!vehicleDetails.getLicensePlate().equals(existingVehicle.getLicensePlate())) {
            if (!isValidLicensePlate(vehicleDetails.getLicensePlate())) {
                throw new RuntimeException("Biển số xe mới không hợp lệ!");
            }
            if (vehicleRepository.existsByLicensePlate(vehicleDetails.getLicensePlate())) {
                throw new RuntimeException("Biển số xe này đã được sử dụng bởi phương tiện khác!");
            }
        }

        existingVehicle.setLicensePlate(vehicleDetails.getLicensePlate());
        existingVehicle.setName(vehicleDetails.getName());
        existingVehicle.setType(vehicleDetails.getType());
        existingVehicle.setTotalSeats(vehicleDetails.getTotalSeats());
        existingVehicle.setFloors(vehicleDetails.getFloors());
        existingVehicle.setImageUrl(vehicleDetails.getImageUrl());
        existingVehicle.setActive(vehicleDetails.isActive());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        Vehicle existingVehicle = getVehicleById(id);
        vehicleRepository.delete(existingVehicle);
    }
}
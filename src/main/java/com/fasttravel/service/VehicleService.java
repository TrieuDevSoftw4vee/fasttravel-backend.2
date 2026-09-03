package com.fasttravel.service;

import com.fasttravel.entity.Vehicle;
import java.util.List;

public interface VehicleService {
    boolean isValidLicensePlate(String plate);
    List<Vehicle> getAllVehicles();
    Vehicle getVehicleById(Long id);
    Vehicle createVehicle(Vehicle vehicle);
    Vehicle updateVehicle(Long id, Vehicle vehicleDetails);
    void deleteVehicle(Long id);
}
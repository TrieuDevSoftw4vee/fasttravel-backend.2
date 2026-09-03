package com.fasttravel.controller;

import com.fasttravel.entity.Vehicle;
import com.fasttravel.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vehicles")
@Tag(name = "Admin - Vehicle Management", description = "CRUD Quản lý phương tiện xe khách theo Database mới")
public class AdminVehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Operation(summary = "Lấy danh sách tất cả phương tiện")
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @Operation(summary = "Lấy thông tin phương tiện theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @Operation(summary = "Thêm phương tiện mới kèm validate biển số")
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicle), HttpStatus.CREATED);
    }

    @Operation(summary = "Cập nhật thông tin phương tiện")
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @Operation(summary = "Xóa phương tiện")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Đã xóa phương tiện thành công!");
    }
}
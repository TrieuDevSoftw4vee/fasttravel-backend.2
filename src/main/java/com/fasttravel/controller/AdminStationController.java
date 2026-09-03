package com.fasttravel.controller;

import com.fasttravel.entity.Station;
import com.fasttravel.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stations")
@Tag(name = "Admin - Station Management", description = "CRUD Quản lý bến xe theo cơ sở dữ liệu mới")
public class AdminStationController {

    @Autowired
    private StationService stationService;

    @Operation(summary = "Lấy danh sách tất cả bến xe")
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @Operation(summary = "Lấy thông tin bến xe theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    @Operation(summary = "Thêm bến xe mới")
    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        return new ResponseEntity<>(stationService.createStation(station), HttpStatus.CREATED);
    }

    @Operation(summary = "Cập nhật thông tin bến xe")
    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable Long id, @RequestBody Station station) {
        return ResponseEntity.ok(stationService.updateStation(id, station));
    }

    @Operation(summary = "Xóa bến xe")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.ok("Đã xóa bến xe thành công!");
    }
}
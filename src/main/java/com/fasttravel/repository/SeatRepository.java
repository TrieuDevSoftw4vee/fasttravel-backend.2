package com.fasttravel.repository;

import com.fasttravel.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Spring Data JPA sẽ tự động build câu query: SELECT * FROM seats WHERE vehicle_id = ?
    List<Seat> findByVehicleId(Long vehicleId);

}
package com.fasttravel.repository;

import com.fasttravel.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByActiveTrueOrderByProvinceNameAscNameAsc();
    boolean existsByName(String name);
}

package com.fasttravel.service;

import com.fasttravel.entity.Seat;
import com.fasttravel.entity.Vehicle;
import com.fasttravel.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatGenerationService {

    @Autowired
    private SeatRepository seatRepository;

    @Transactional
    public void generateSeatsForVehicle(Vehicle vehicle) {
        List<Seat> seats = new ArrayList<>();
        int totalSeats = vehicle.getTotalSeats();
        String type = vehicle.getType().name(); // SLEEPER, LIMOUSINE, SEATER
        int floors = vehicle.getFloors();

        if ("SLEEPER".equals(type) || floors == 2) {
            // Logic cho xe 2 tầng (Ví dụ: Giường nằm 34 chỗ chia đều 2 tầng)
            int seatsPerFloor = totalSeats / 2;
            for (int i = 1; i <= totalSeats; i++) {
                Seat seat = new Seat();
                seat.setVehicle(vehicle);

                // Đặt tên ghế: Tầng 1 từ A01-A17, Tầng 2 từ B01-B17...
                int floor = (i <= seatsPerFloor) ? 1 : 2;
                int seatIndexInFloor = (floor == 1) ? i : (i - seatsPerFloor);
                String prefix = (floor == 1) ? "A" : "B";
                seat.setSeatNumber(String.format("%s%02d", prefix, seatIndexInFloor));

                seat.setFloor(floor);
                // Sắp xếp bố cục 4 cột (mỗi hàng 4 ghế)
                seat.setRowIndex(((seatIndexInFloor - 1) / 4) + 1);
                seat.setColumnIndex(((seatIndexInFloor - 1) % 4) + 1);
                seat.setSeatType("STANDARD");
                seat.setActive(true);

                seats.add(seat);
            }
        } else {
            // Logic cho xe Limousine / 1 tầng (Ví dụ: 22 chỗ ký hiệu L01, L02...)
            for (int i = 1; i <= totalSeats; i++) {
                Seat seat = new Seat();
                seat.setVehicle(vehicle);

                seat.setSeatNumber(String.format("L%02d", i));
                seat.setFloor(1);
                seat.setRowIndex(((i - 1) / 4) + 1);
                seat.setColumnIndex(((i - 1) % 4) + 1);
                seat.setSeatType("VIP");
                seat.setActive(true);

                seats.add(seat);
            }
        }

        // Lưu toàn bộ danh sách ghế xuống database
        seatRepository.saveAll(seats);
    }
}
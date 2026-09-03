package com.fasttravel.config;

import com.fasttravel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    @Scheduled(fixedDelay = 60000)
    public void autoReleaseExpiredBookings() {
        try {
            bookingService.releaseExpired();
            log.info("Đã thực thi quét và dọn dẹp các đơn đặt vé quá hạn thanh toán.");
        } catch (Exception e) {
            log.error("Lỗi tự động hủy vé quá hạn: {}", e.getMessage());
        }
    }
}
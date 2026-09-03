package com.fasttravel.config;

import com.fasttravel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingService service;

    @Scheduled(fixedDelay = 60000)
    public void release() {
        service.releaseExpired();
    }
}

package com.fasttravel.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getOverview(LocalDateTime start, LocalDateTime end);
}
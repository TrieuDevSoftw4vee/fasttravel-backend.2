package com.fasttravel.service;

import java.time.LocalDate;
import java.util.*;

public interface TripService {
    List<Map<String, Object>> stations();

    List<Map<String, Object>> search(Long origin, Long destination, LocalDate date);

    Map<String, Object> detail(Long id);

    List<Map<String, Object>> seats(Long tripId);
}

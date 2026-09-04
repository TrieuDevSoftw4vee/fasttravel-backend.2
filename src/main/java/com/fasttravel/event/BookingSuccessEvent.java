package com.fasttravel.event;

import com.fasttravel.entity.Booking;

public record BookingSuccessEvent(Booking booking) {}
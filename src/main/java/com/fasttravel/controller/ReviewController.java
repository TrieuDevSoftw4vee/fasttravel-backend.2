package com.fasttravel.controller;

import com.fasttravel.dao.BookingDAO;
import com.fasttravel.dto.ApiResponse;
import com.fasttravel.entity.*;
import com.fasttravel.exception.AppException;
import com.fasttravel.repository.TripReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final TripReviewRepository reviewRepository;
    private final BookingDAO bookingDAO;

    @PostMapping
    public ApiResponse<?> createReview(Authentication auth,
                                       @RequestParam Long bookingId,
                                       @RequestParam Integer rating,
                                       @RequestParam(required = false) String comment) {
        Long userId = (Long) auth.getDetails();

        if (rating < 1 || rating > 5) {
            throw AppException.bad("Đánh giá phải từ 1 đến 5 sao");
        }

        Booking booking = bookingDAO.findById(bookingId)
                .orElseThrow(() -> AppException.notFound("Đơn hàng không tồn tại"));

        if (!booking.getUser().getId().equals(userId)) {
            throw AppException.bad("Bạn không thể đánh giá đơn hàng của người khác");
        }

        if (booking.getTrip().getStatus() != Trip.Status.FINISHED) {
            throw AppException.bad("Chỉ được đánh giá sau khi chuyến xe đã HOÀN THÀNH (FINISHED)");
        }

        if (booking.getStatus() != Booking.Status.PAID) {
            throw AppException.bad("Đơn hàng chưa thanh toán thành công không thể thực hiện đánh giá");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw AppException.conflict("Đơn hàng này đã được đánh giá trước đó");
        }

        TripReview review = new TripReview();
        review.setUser(booking.getUser());
        review.setTrip(booking.getTrip());
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);

        return ApiResponse.ok("Gửi đánh giá chuyến đi thành công", reviewRepository.save(review));
    }

    @GetMapping("/trip/{tripId}")
    public ApiResponse<?> getReviewsByTrip(@PathVariable Long tripId) {
        return ApiResponse.ok(reviewRepository.findByTripIdOrderByCreatedAtDesc(tripId));
    }
}
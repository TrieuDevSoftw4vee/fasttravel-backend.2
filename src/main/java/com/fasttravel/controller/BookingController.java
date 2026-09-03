package com.fasttravel.controller;

import com.fasttravel.dto.ApiResponse;
import com.fasttravel.dto.BookingDTO.*;
import com.fasttravel.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    private Long uid(Authentication a) {
        return (Long) a.getDetails();
    }

    @PostMapping
    public ApiResponse<?> create(Authentication a, @Valid @RequestBody CreateRequest r, HttpServletRequest q) {
        return ApiResponse.ok("Đã giữ ghế trong 15 phút", service.create(uid(a), r, q.getRemoteAddr()));
    }

    @GetMapping
    public ApiResponse<?> history(Authentication a) {
        return ApiResponse.ok(service.history(uid(a)));
    }

    @GetMapping("/vnpay-return")
    public ApiResponse<?> vnpayReturn(
            @RequestParam("vnp_TxnRef") String code,
            @RequestParam("vnp_TransactionNo") String gateway,
            @RequestParam("vnp_ResponseCode") String response,
            @RequestParam("vnp_Amount") long vnpAmount,
            @RequestParam("vnp_BankCode") String bankCode) {

        service.confirmPayment(code, gateway, response, vnpAmount, bankCode);
        return ApiResponse.ok("Xử lý giao dịch VNPay hoàn tất", null);
    }

    @PostMapping("/{code}/cancel")
    public ApiResponse<?> cancel(
            Authentication a,
            @PathVariable("code") String code,
            @RequestParam("reason") String reason) {

        return ApiResponse.ok("Hủy vé thành công", service.cancelBooking(uid(a), code, reason));
    }
}
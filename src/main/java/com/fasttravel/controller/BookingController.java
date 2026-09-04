package com.fasttravel.controller;

import com.fasttravel.dto.ApiResponse;
import com.fasttravel.dto.BookingDTO.*;
import com.fasttravel.exception.AppException;
import com.fasttravel.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.fasttravel.util.VnPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;
    private final VnPayUtil vnpayUtil;

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
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(
            @RequestParam Map<String, String> params,
            @RequestParam("vnp_TxnRef") String code,
            @RequestParam("vnp_TransactionNo") String gateway,
            @RequestParam("vnp_ResponseCode") String response,
            @RequestParam("vnp_Amount") long vnpAmount,
            @RequestParam("vnp_BankCode") String bankCode) {

        try {
            // 1. Xác thực chữ ký bằng VnPayUtil (Bắt buộc inject VnPayUtil vào Controller)
            if (!vnpayUtil.valid(params)) {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
            }

            // 2. Gọi logic xử lý từ Service
            service.confirmPayment(code, gateway, response, vnpAmount, bankCode);

            // 3. Trả về mã thành công cho Server VNPay
            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));

        } catch (AppException e) {
            String msg = e.getMessage();

            // Map các lỗi từ BookingServiceImpl sang mã RspCode chuẩn của VNPay
            if (msg.contains("không tồn tại")) {
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
            }
            if (msg.contains("không khớp")) {
                return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid amount"));
            }
            if (msg.contains("không còn ở trạng thái chờ")) {
                return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
            }

            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
        }
    }
}
package com.fasttravel.controller;

import com.fasttravel.dto.ApiResponse;
import com.fasttravel.exception.AppException;
import com.fasttravel.service.BookingService;
import com.fasttravel.util.VnPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final VnPayUtil vnpay;
    private final BookingService bookings;

    @GetMapping("/vnpay-return")
    public ApiResponse<?> callback(@RequestParam Map<String, String> p) {
        if (!vnpay.valid(p)) throw AppException.bad("Chữ ký VNPay không hợp lệ");
        if (!vnpay.isCorrectTmnCode(p.get("vnp_TmnCode"))) throw AppException.bad("Sai mã website VNPay (vnp_TmnCode)");

        String code = p.get("vnp_TxnRef");
        String responseCode = p.get("vnp_ResponseCode");
        String transactionStatus = p.get("vnp_TransactionStatus");
        String gatewayTransaction = p.getOrDefault("vnp_TransactionNo", "");
        String bankCode = p.get("vnp_BankCode");

        long vnpAmount;
        try {
            vnpAmount = Long.parseLong(p.getOrDefault("vnp_Amount", "-1"));
        } catch (NumberFormatException e) {
            throw AppException.bad("Số tiền VNPay trả về không hợp lệ");
        }

        // Phiên bản 2.1.0 trả thêm vnp_TransactionStatus; cả hai mã phải là 00 mới coi là thành công.
        String finalResponse = "00".equals(responseCode) && (transactionStatus == null || "00".equals(transactionStatus))
                ? "00" : responseCode;

        bookings.confirmPayment(code, gatewayTransaction, finalResponse, vnpAmount, bankCode);
        return ApiResponse.ok("Thanh toán VNPay thành công", Map.of(
                "bookingCode", code,
                "responseCode", responseCode,
                "transactionNo", gatewayTransaction
        ));
    }

}

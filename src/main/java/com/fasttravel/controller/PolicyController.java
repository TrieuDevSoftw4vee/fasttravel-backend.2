package com.fasttravel.controller;

import com.fasttravel.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/policies")
public class PolicyController {

    @GetMapping
    public ApiResponse<?> getPolicies() {
        var terms = Map.of(
                "title", "Điều khoản & Điều kiện Sử dụng FastTravel",
                "content", List.of(
                        "Khách hàng vui lòng có mặt tại trạm đón trước giờ khởi hành tối thiểu 20 phút.",
                        "Xuất trình vé điện tử hoặc mã QR trên ứng dụng cho tài xế/phụ xe khi lên xe.",
                        "Hành lý miễn cước tối đa 20kg mỗi hành khách."
                )
        );

        var cancellation = Map.of(
                "title", "Quy định Hủy vé & Mức Hoàn tiền",
                "content", List.of(
                        "Hủy trước giờ khởi hành > 24 tiếng: Hoàn 90% tổng tiền vé.",
                        "Hủy trước giờ khởi hành từ 12 đến 24 tiếng: Hoàn 70% tổng tiền vé.",
                        "Hủy trước giờ khởi hành từ 3 đến 12 tiếng: Hoàn 50% tổng tiền vé.",
                        "Dưới 3 tiếng trước giờ khởi hành: Không hỗ trợ hủy vé và hoàn tiền."
                )
        );

        return ApiResponse.ok(Map.of("terms", terms, "cancellationPolicy", cancellation));
    }
}
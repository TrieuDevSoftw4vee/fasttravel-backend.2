package com.fasttravel.util;

import com.fasttravel.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class VnPayUtil {
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNP_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${app.vnpay.tmn-code}")
    String tmn;
    @Value("${app.vnpay.hash-secret}")
    String secret;
    @Value("${app.vnpay.pay-url}")
    String payUrl;
    @Value("${app.vnpay.return-url}")
    String returnUrl;

    public String create(String code, long amount, String ip) {
        validateSandboxConfig();
        if (amount <= 0) throw AppException.bad("Số tiền thanh toán VNPay phải lớn hơn 0");

        Map<String, String> p = new TreeMap<>();
        p.put("vnp_Version", "2.1.0");
        p.put("vnp_Command", "pay");
        p.put("vnp_TmnCode", tmn.trim());
        p.put("vnp_Amount", String.valueOf(amount * 100));
        p.put("vnp_CurrCode", "VND");
        p.put("vnp_TxnRef", code);
        p.put("vnp_OrderInfo", "Thanh toan ve FastTravel " + code);
        p.put("vnp_OrderType", "other");
        p.put("vnp_Locale", "vn");
        p.put("vnp_ReturnUrl", returnUrl);
        p.put("vnp_IpAddr", normalizeIp(ip));

        LocalDateTime now = LocalDateTime.now(VN_ZONE);
        p.put("vnp_CreateDate", now.format(VNP_DATE));
        p.put("vnp_ExpireDate", now.plusMinutes(15).format(VNP_DATE));

        String signData = buildSignData(p);
        String query = buildQuery(p);
        return payUrl + "?" + query + "&vnp_SecureHash=" + hmac(signData);
    }

    public boolean valid(Map<String, String> input) {
        validateSandboxConfig();
        String hash = input.get("vnp_SecureHash");
        if (hash == null || hash.isBlank()) return false;

        Map<String, String> p = new TreeMap<>(input);
        p.remove("vnp_SecureHash");
        p.remove("vnp_SecureHashType");
        return hash.equalsIgnoreCase(hmac(buildSignData(p)));
    }

    public boolean isCorrectTmnCode(String receivedTmnCode) {
        return receivedTmnCode != null && receivedTmnCode.equals(tmn.trim());
    }

    private String buildSignData(Map<String, String> params) {
        // Khớp mẫu Java chính thức của VNPay 2.1.0:
        // key không encode trong chuỗi ký; value dùng URLEncoder (space => '+').
        return params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(e -> e.getKey() + "=" + enc(e.getValue()))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    private String buildQuery(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    private String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.US_ASCII);
    }

    private String hmac(String data) {
        try {
            Mac m = Mac.getInstance("HmacSHA512");
            m.init(new SecretKeySpec(secret.trim().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return HexFormat.of().formatHex(m.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Không thể tạo chữ ký VNPay", e);
        }
    }

    private String normalizeIp(String ip) {
        if (ip == null || ip.isBlank() || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";
        int comma = ip.indexOf(',');
        return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
    }

    private void validateSandboxConfig() {
        if (tmn == null || tmn.isBlank() || "DEMO".equalsIgnoreCase(tmn.trim()) || tmn.trim().startsWith("your-")) {
            throw AppException.bad("VNPay chưa được cấu hình: hãy nhập VNPAY_TMN_CODE Sandbox thật trong biến môi trường");
        }
        if (secret == null || secret.isBlank() || "DEMO_SECRET".equalsIgnoreCase(secret.trim()) || secret.trim().startsWith("your-")) {
            throw AppException.bad("VNPay chưa được cấu hình: hãy nhập VNPAY_HASH_SECRET Sandbox thật trong biến môi trường");
        }
    }
}

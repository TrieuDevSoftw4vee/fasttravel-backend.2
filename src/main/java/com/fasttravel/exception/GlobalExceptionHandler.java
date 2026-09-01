package com.fasttravel.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    ResponseEntity<?> app(AppException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("success", false, "message", e.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> valid(MethodArgumentNotValidException e) {
        var x = new LinkedHashMap<String, String>();
        e.getBindingResult().getFieldErrors().forEach(f -> x.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Dữ liệu không hợp lệ", "errors", x));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> other(Exception e) {
        return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage() == null ? "Lỗi hệ thống" : e.getMessage()));
    }
}

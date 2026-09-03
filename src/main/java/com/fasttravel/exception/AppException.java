package com.fasttravel.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final HttpStatus status;

    public AppException(HttpStatus s, String m) {
        super(m);
        status = s;
    }

    public static AppException notFound(String m) {
        return new AppException(HttpStatus.NOT_FOUND, m);
    }

    public static AppException bad(String m) {
        return new AppException(HttpStatus.BAD_REQUEST, m);
    }

    public static AppException conflict(String m) {
        return new AppException(HttpStatus.CONFLICT, m);
    }
}

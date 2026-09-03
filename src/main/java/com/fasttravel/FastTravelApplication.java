package com.fasttravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FastTravelApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastTravelApplication.class, args);
    }
}

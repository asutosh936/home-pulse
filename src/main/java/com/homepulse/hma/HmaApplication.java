package com.homepulse.hma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmaApplication.class, args);
    }
}

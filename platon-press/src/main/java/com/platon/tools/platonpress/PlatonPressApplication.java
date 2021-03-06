package com.platon.tools.platonpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PlatonPressApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatonPressApplication.class, args);
    }
}

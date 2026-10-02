package com.atharva.nutricheckai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class NutriCheckAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriCheckAiApplication.class, args);
    }

}

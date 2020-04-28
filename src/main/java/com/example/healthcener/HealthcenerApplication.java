package com.example.healthcener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"web"})
public class HealthcenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcenerApplication.class, args);
    }

}

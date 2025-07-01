package com.example.tracingtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TracingTestApplication {
    
    
    public static void main(String[] args) {
        SpringApplication.run(TracingTestApplication.class, args);
    }
    

    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }
}

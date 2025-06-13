package com.example.tracingtest;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@SpringBootApplication
public class TracingTestApplication {
    
    @Autowired(required = false)
    private List<MeterRegistry> meterRegistries;
    
    public static void main(String[] args) {
        SpringApplication.run(TracingTestApplication.class, args);
    }
    
    // @EventListener(ApplicationReadyEvent.class)
    // public void checkMeterRegistries() {
    //     System.out.println("========== METER REGISTRIES DEBUG ===========");
    //     if (meterRegistries == null || meterRegistries.isEmpty()) {
    //         System.out.println("No MeterRegistry beans found!");
    //     } else {
    //         System.out.println("Found " + meterRegistries.size() + " MeterRegistry beans:");
    //         for (MeterRegistry registry : meterRegistries) {
    //             System.out.println("  - " + registry.getClass().getName());
    //         }
    //     }
    //     System.out.println("==============================================");
    // }

    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }
}

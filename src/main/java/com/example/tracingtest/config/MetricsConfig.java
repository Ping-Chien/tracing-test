package com.example.tracingtest.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {
    
    @Value("${management.otlp.metrics.export.url:http://otel-collector.opentelemetry.svc.cluster.local:4318}")
    private String otlpUrl;
    
    @Value("${management.otlp.metrics.export.step:60s}")
    private String stepDuration;
    
    @Value("${spring.application.name:tracing-test}")
    private String applicationName;
    
    @Value("${management.otlp.metrics.export.enabled:true}")
    private boolean otlpEnabled;
    
    @Bean
    public OtlpMeterRegistry otlpMeterRegistry() {
        if (!otlpEnabled) {
            System.out.println("OTLP metrics export is disabled");
            return null;
        }
        
        System.out.println("========== CREATING OTLP METER REGISTRY ===========");
        System.out.println("OTLP URL: " + otlpUrl);
        System.out.println("Step Duration: " + stepDuration);
        System.out.println("Application Name: " + applicationName);
        System.out.println("OTLP Enabled: " + otlpEnabled);
        System.out.println("===================================================");
        
        OtlpConfig config = new OtlpConfig() {
            @Override
            public String get(String key) {
                // 添加一些關鍵配置
                if ("otlp.resource.attributes".equals(key)) {
                    return "service.name=" + applicationName + ",service.version=1.0.0";
                }
                return null; // Accept defaults for most settings
            }
            
            @Override
            public String url() {
                // 確保 URL 包含 /v1/metrics 路徑
                String fullUrl = otlpUrl;
                if (!fullUrl.endsWith("/v1/metrics")) {
                    fullUrl = fullUrl + "/v1/metrics";
                }
                System.out.println("Final OTLP URL: " + fullUrl);
                return fullUrl;
            }
            
            @Override
            public Duration step() {
                try {
                    // 解析步驟持續時間 (例如 "60s" -> PT60S)
                    String duration = stepDuration.toUpperCase();
                    if (!duration.startsWith("PT")) {
                        duration = "PT" + duration;
                    }
                    Duration parsed = Duration.parse(duration);
                    System.out.println("Parsed step duration: " + parsed);
                    return parsed;
                } catch (Exception e) {
                    System.err.println("Error parsing step duration '" + stepDuration + "', using default 1 minute: " + e.getMessage());
                    return Duration.ofMinutes(1);
                }
            }
        };
        
        OtlpMeterRegistry registry = new OtlpMeterRegistry(config, Clock.SYSTEM);
        System.out.println("OtlpMeterRegistry created successfully!");
        return registry;
    }
}

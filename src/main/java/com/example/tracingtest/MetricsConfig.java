package com.example.tracingtest;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 由於javaagent 的 OpenTelemetryMeterRegistry 沒有自動註冊到 MeterRegistry，
 * 因此手動註冊，讓java agent 可以攔截到 micrommeter 收集到的指標。
 */
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {

        CompositeMeterRegistry meterRegistry = new CompositeMeterRegistry();
        // 拿到全局 OpenTelemetry 實例（Java Agent 啟動後會設置）
        var openTelemetry = GlobalOpenTelemetry.get();

        MeterRegistry openTelemetryMeterRegistry = OpenTelemetryMeterRegistry.builder(openTelemetry).build();

        meterRegistry.add(openTelemetryMeterRegistry);

        return meterRegistry;
    }
}

package com.example.tracingtest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracingtest.otel.CounterUsage;
import com.example.tracingtest.otel.GaugeUsage;
import com.example.tracingtest.otel.HistogramUsage;
import com.example.tracingtest.otel.UpDownCounterUsage;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.Meter;



@RestController
public class MetricController {

    private static final Logger log = LoggerFactory.getLogger(MetricController.class);
    private static Meter meter = GlobalOpenTelemetry.getMeter("com.example.custome_metric");
    
    @GetMapping("/custom-metric")
    public String click() {
        log.info("===== custom metric endpoint called =====");
        try {
            CounterUsage.counterUsage(meter);
            GaugeUsage.gaugeUsage(meter);
            HistogramUsage.histogramUsage(meter);
            UpDownCounterUsage.usage(meter);
            log.info("===== custom metric endpoint return =====");
         return "OK";
        } catch (Exception e) {
            log.error("custom metric fail: {}", e.getMessage());
            return "custom metric fail: " + e.getMessage();
        }
    }

}

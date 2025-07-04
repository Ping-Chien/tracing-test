package com.example.tracingtest.config;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
@Configuration
public class MetricsConfig {
 
    private static final Logger log = LoggerFactory.getLogger(MetricsConfig.class);
   
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
            log.info("OTLP metrics export is disabled");
            return null;
        }
       
        log.info("========== CREATING OTLP METER REGISTRY ===========");
        log.info("OTLP URL: {}", otlpUrl);
        log.info("Step Duration: {}", stepDuration);
        log.info("Application Name: {}", applicationName);
        log.info("OTLP Enabled: {}", otlpEnabled);
        log.info("===================================================");
     
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
                log.info("Final OTLP URL: {}", fullUrl);
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
                    log.info("Parsed step duration: {}", parsed);
                    return parsed;
                } catch (Exception e) {
                    log.error("Error parsing step duration '{}', using default 1 minute: {}", stepDuration, e.getMessage());
                    return Duration.ofMinutes(1);
                }
            }
        };
     
        // 創建一個擴展的 OtlpMeterRegistry，覆寫 publish 方法來在真正發送前記錄日誌
        OtlpMeterRegistry registry = new OtlpMeterRegistry(config, Clock.SYSTEM) {
            @Override
            protected void publish() {
                // 在批次發送前記錄所有指標的摘要信息
                List<Meter> meters = new ArrayList<>();
                this.getMeters().forEach(meters::add);
             
                if (!meters.isEmpty()) {
                    log.info("===== 準備批次發送 {} 個指標到 OTLP Collector: {} =====", meters.size(), otlpUrl);
                 
                    // 如果需要詳細列出每個指標，可以啟用下面的代碼
                    meters.forEach(meter -> logMetricDetail(meter));

                }
             
                // 調用父類方法完成實際發送
                super.publish();
             
                // 發送完成後記錄
                log.info("===== 指標發送完成 =====");
            }
        };

        
        // 启用Timer直方图
        registry.config().meterFilter(
            new MeterFilter() {
                @Override
                public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                    if (id.getType() == Meter.Type.TIMER) {
                        // 启用百分位直方图并设置关键百分位值
                        return DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .percentiles(0.5, 0.95, 0.99)
                            .build()
                            .merge(config);
                    }
                    return config;
                }
            }
        );
     
        log.info("OtlpMeterRegistry created successfully!");
        return registry;
    }
 
    /**
     * 記錄指標的詳細信息，包含測量值
     */
    private void logMetricDetail(Meter meter) {
        Meter.Id id = meter.getId();
     
        StringBuilder tags = new StringBuilder();
        for (Tag tag : id.getTags()) {
            tags.append(tag.getKey()).append("=").append(tag.getValue()).append(", ");
        }
     
        String tagsStr = tags.length() > 0 ? tags.substring(0, tags.length() - 2) : "";
     
        StringBuilder sb = new StringBuilder();
        sb.append("\n指標: ").append(id.getName())
          .append(" [類型: ").append(id.getType()).append("]")
          .append(" [標籤: ").append(tagsStr).append("]\n")
          .append("  測量值:\n");
     
        // 添加測量值
        meter.measure().forEach(measurement -> {
            sb.append("    - ").append(measurement.getStatistic().name())
              .append(" = ").append(measurement.getValue()).append("\n");
        });
     
        log.info(sb.toString());
    }
}

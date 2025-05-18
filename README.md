# tracing-test

Spring Boot 專案，使用 Java 17、Spring Boot 2.5 以上版本，Gradle 管理依賴。
此專案會建立兩個docker image
- tracing-test: Spring Boot application，測試以改code方式來推送observability data
- javaagent: 測試使用 opentelemetry java agent 來收observability data

## 建置與執行

```bash
./gradlew bootRun
```

## 建立 Docker image
```bash
./build-image.sh
```

## 推送到GCP artifact registry
> 請到 terraform-gcp 專案跟目錄，執行 `./sh/push-image-to-artifact-registry.sh

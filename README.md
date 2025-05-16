# tracing-test

Spring Boot 專案，使用 Java 17、Spring Boot 2.5 以上版本，Gradle 管理依賴。

## 建置與執行

```bash
./gradlew bootRun
```

## 測試

```bash
./gradlew test
```

## 建立 Docker image
```bash
./build-image.sh
```

## 推送到GCP artifact registry
> 請到 terraform-gcp 專案，執行 `/sh/push-image-to-artifact-registry.sh

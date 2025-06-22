#!/bin/bash

# 參數：可自訂 app image tag，預設為 tracing-test:pull-mode
APP_TAG=${1:-tracing-test:pull-mode}


# 編譯 Spring Boot 專案
./gradlew clean build -x test

# 檢查 jar 是否存在
if [ ! -f build/libs/tracing-test-0.0.1-SNAPSHOT.jar ]; then
  echo "JAR 檔案不存在，請確認 build 是否成功。"
  exit 1
fi

# 建立 Spring Boot app Docker image

docker build -t $APP_TAG .

if [ $? -eq 0 ]; then
  echo "Spring Boot app Docker image 已建立，tag: $APP_TAG"
else
  echo "Spring Boot app Docker build 失敗"
  exit 1
fi
#!/bin/bash

# 參數：可自訂 tag，預設為 tracing-test:latest
TAG=${1:-tracing-test:latest}

# 編譯 Spring Boot 專案
./gradlew clean build -x test

# 檢查 jar 是否存在
if [ ! -f build/libs/tracing-test-0.0.1-SNAPSHOT.jar ]; then
  echo "JAR 檔案不存在，請確認 build 是否成功。"
  exit 1
fi

# 建立 Docker image

docker build -t $TAG .

if [ $? -eq 0 ]; then
  echo "Docker image 已建立，tag: $TAG"
else
  echo "Docker build 失敗"
  exit 1
fi

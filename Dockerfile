# Start with a lightweight OpenJDK image
FROM eclipse-temurin:17-jre-alpine

# 安裝 curl
RUN apk add --no-cache curl

# Set the working directory
WORKDIR /app

# Copy the built jar from the build context
COPY build/libs/tracing-test-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

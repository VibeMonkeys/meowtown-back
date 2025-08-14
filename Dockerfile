# Multi-stage build for Railway
FROM gradle:8.11-jdk21 AS builder

WORKDIR /app

# Copy build files
COPY build.gradle gradle.properties ./
COPY src ./src

# Build the application (skip tests for faster build)
RUN gradle bootJar --no-daemon -x test

# Runtime stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set production profile
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Run the application with optimized JVM settings
CMD ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
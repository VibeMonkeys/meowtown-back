# Multi-stage build for optimized production image
FROM gradle:8-jdk21-alpine AS build

# Set working directory
WORKDIR /app

# Copy gradle files for dependency caching
COPY build.gradle gradle.properties ./
COPY gradle gradle
COPY gradlew ./

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Production stage
FROM eclipse-temurin:21-jre-alpine

# Create app user for security (Alpine Linux)
RUN addgroup -g 1000 app && adduser -D -s /bin/sh -u 1000 -G app app

# Install required packages (Alpine)
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to app user
RUN chown app:app /app/app.jar

# Switch to non-root user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]
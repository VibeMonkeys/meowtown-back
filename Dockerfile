# 간단한 Dockerfile (Railway용)
FROM openjdk:21-jdk-slim

WORKDIR /app

# Gradle wrapper 복사
COPY gradlew .
COPY gradle gradle/
RUN chmod +x gradlew

# 빌드 파일 복사
COPY build.gradle .
COPY gradle.properties .

# 소스 복사
COPY src src/

# 빌드
RUN ./gradlew bootJar --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/*.jar"]
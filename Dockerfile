# 단일 스테이지 빌드 (Railway 최적화)
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 시스템 업데이트 및 필요한 패키지 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Gradle Wrapper 복사 및 실행 권한 부여
COPY gradlew ./
COPY gradle gradle/
RUN chmod +x gradlew

# 빌드 파일 복사
COPY build.gradle gradle.properties ./

# 소스 코드 복사
COPY src src/

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon --parallel

# 포트 노출
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx512m -XX:+UseContainerSupport"

# 애플리케이션 실행
CMD ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar build/libs/*.jar"]
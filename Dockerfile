# Railway용 Dockerfile (상세 로깅)
FROM openjdk:21-jdk-slim

WORKDIR /app

# 시스템 업데이트
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Gradle wrapper 복사
COPY gradlew .
COPY gradle gradle/
RUN chmod +x gradlew

# 빌드 파일 복사
COPY build.gradle .
COPY gradle.properties .

# Gradle 정보 출력
RUN ./gradlew --version

# 소스 복사
COPY src src/

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon --info

# 빌드 (상세 로그)
RUN ./gradlew bootJar --no-daemon --info --stacktrace

# JAR 파일 확인
RUN ls -la build/libs/

EXPOSE 8080

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=prod

# JAR 실행
CMD ["sh", "-c", "java -Xmx512m -jar build/libs/*.jar"]
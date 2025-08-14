# MeowTown Docker 개발 환경

MeowTown 백엔드 개발을 위한 Docker Compose 환경입니다.

## 🐳 포함된 서비스

- **PostgreSQL 16** (PostGIS 확장 포함) - 메인 데이터베이스
- **Redis 7.2** - 캐시 및 세션 저장소
- **pgAdmin 4** - PostgreSQL 관리 도구
- **Redis Insight** - Redis 관리 도구

## 🚀 빠른 시작

### 1. 전체 개발 환경 시작
```bash
# 편리한 스크립트 사용
./docker-dev.sh start

# 또는 직접 docker-compose 사용
docker-compose up -d
```

### 2. 데이터베이스만 시작 (권장)
```bash
# DB와 Redis만 시작
./docker-dev.sh db-only
```

### 3. Spring Boot 애플리케이션 실행
```bash
./gradlew bootRun
```

## 🔧 관리 명령어

```bash
# 개발 환경 관리
./docker-dev.sh start     # 전체 환경 시작
./docker-dev.sh stop      # 환경 중지
./docker-dev.sh restart   # 재시작
./docker-dev.sh status    # 상태 확인
./docker-dev.sh logs      # 로그 확인

# 데이터 관리
./docker-dev.sh db-only   # DB만 시작
./docker-dev.sh reset     # DB 리셋
./docker-dev.sh clean     # 모든 데이터 삭제

# 도움말
./docker-dev.sh help
```

## 🔌 접속 정보

### PostgreSQL
- **Host**: localhost
- **Port**: 5432
- **Database**: meowtown_dev
- **Username**: meowtown
- **Password**: meowtown123
- **Extensions**: PostGIS, UUID

### Redis
- **Host**: localhost
- **Port**: 6379
- **Password**: (없음)

### 관리 도구

#### pgAdmin
- **URL**: http://localhost:8081
- **Email**: admin@meowtown.local
- **Password**: admin123

#### Redis Insight
- **URL**: http://localhost:8082

## 📝 데이터베이스 설정

애플리케이션의 `application-dev.yml`이 Docker 환경과 연동되도록 설정되어 있습니다:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/meowtown_dev
    username: meowtown
    password: meowtown123
    driver-class-name: org.postgresql.Driver
    
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

## 🛠️ 트러블슈팅

### 1. 포트 충돌 문제
```bash
# 사용 중인 포트 확인
lsof -i :5432  # PostgreSQL
lsof -i :6379  # Redis
lsof -i :8081  # pgAdmin
lsof -i :8082  # Redis Insight

# 충돌하는 프로세스 종료
kill -9 <PID>
```

### 2. Docker 볼륨 문제
```bash
# 데이터 완전 삭제 후 재시작
./docker-dev.sh clean
./docker-dev.sh start
```

### 3. PostgreSQL 연결 문제
```bash
# 컨테이너 로그 확인
docker logs meowtown-postgres

# 데이터베이스 상태 확인
docker exec -it meowtown-postgres psql -U meowtown -d meowtown_dev -c "SELECT version();"
```

### 4. PostGIS 확장 확인
```bash
# PostGIS 설치 확인
docker exec -it meowtown-postgres psql -U meowtown -d meowtown_dev -c "SELECT PostGIS_Version();"
```

## 📊 데이터 백업 및 복원

### 백업
```bash
# 데이터베이스 백업
docker exec -t meowtown-postgres pg_dump -U meowtown meowtown_dev > backup.sql

# 특정 테이블만 백업
docker exec -t meowtown-postgres pg_dump -U meowtown -t cats meowtown_dev > cats_backup.sql
```

### 복원
```bash
# 데이터베이스 복원
docker exec -i meowtown-postgres psql -U meowtown meowtown_dev < backup.sql
```

## 🔄 개발 워크플로우

1. **개발 환경 시작**
   ```bash
   ./docker-dev.sh db-only
   ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

3. **개발 작업**
   - 코드 변경 시 Spring Boot DevTools가 자동 재시작
   - 데이터베이스 스키마는 Hibernate가 자동 업데이트

4. **테스트**
   ```bash
   ./gradlew test
   ```

5. **종료**
   ```bash
   ./docker-dev.sh stop
   ```

## 🎯 프로덕션 배포

프로덕션 환경에서는 다음을 고려하세요:

1. **보안 강화**: 강력한 비밀번호 사용
2. **데이터 지속성**: 외부 볼륨 마운트
3. **모니터링**: 로그 및 메트릭 수집
4. **백업**: 정기 백업 스케줄링
5. **네트워크**: 방화벽 및 네트워크 보안

## 📱 모바일 개발 지원

React Native나 모바일 앱 개발 시:

```bash
# 모든 인터페이스에서 접근 가능하도록 설정
# docker-compose.yml의 PostgreSQL bind를 0.0.0.0으로 설정 완료
```

IP 주소로 접속:
- PostgreSQL: `<YOUR_IP>:5432`
- Redis: `<YOUR_IP>:6379`
- Spring Boot API: `<YOUR_IP>:8080` (애플리케이션 실행 후)
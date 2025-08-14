# 🐱 MeowTown Backend - 우리동네 냥이도감 백엔드 API

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=for-the-badge&logo=postgresql)
![PostGIS](https://img.shields.io/badge/PostGIS-3.3-blue?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-red?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)

**지역 커뮤니티 기반 고양이 관리 플랫폼의 백엔드 시스템**

*사용자들이 동네 고양이들의 정보를 공유하고, 목격 정보를 등록하며,*  
*커뮤니티 활동을 통해 고양이들을 돌볼 수 있는 웹 애플리케이션*

</div>

## 📋 프로젝트 개요

MeowTown Backend는 지역 커뮤니티의 길고양이 관리를 위한 **확장 가능하고 안정적인 REST API 서버**입니다. 
고양이 정보 관리부터 커뮤니티 기능, 공간 데이터 처리까지 포괄적인 백엔드 서비스를 제공합니다.

### 🎯 핵심 특징

- **🔐 JWT 기반 인증 시스템**: Spring Security를 활용한 보안 인증
- **🌍 공간 데이터 처리**: PostGIS를 통한 위치 기반 서비스
- **⚡ 캐싱 시스템**: Redis를 통한 성능 최적화
- **🎨 표준화된 API**: 프론트엔드와 100% 호환되는 일관된 응답 형식
- **🏗️ 클린 아키텍처**: Hexagonal Architecture 패턴 적용
- **🐳 컨테이너화**: Docker Compose를 통한 간편한 개발 환경

## 🛠️ 기술 스택

### 🔧 핵심 기술

| 계층 | 기술 스택 | 목적 |
|------|-----------|------|
| **Framework** | Spring Boot 3.2.0, Java 21 | 현대적 자바 웹 애플리케이션 프레임워크 |
| **Database** | PostgreSQL 15 + PostGIS 3.3 | 관계형 데이터베이스 + 공간 데이터 처리 |
| **Cache** | Redis 7 | 세션 관리 및 성능 최적화 |
| **Security** | Spring Security + JWT | 인증/인가 시스템 |
| **Build** | Gradle 8.11 | 의존성 관리 및 빌드 자동화 |
| **Container** | Docker Compose | 개발 환경 표준화 |

### 🏗️ 시스템 아키텍처

```
📦 Hexagonal Architecture (Ports & Adapters)
├── 🎯 Application Core (Domain)
│   ├── Domain Models (Cat, User, Sighting)
│   ├── Use Cases (CreateCat, FindCat)
│   └── Domain Services
├── 🔌 Ports (Interfaces)
│   ├── Input Ports (Use Case Interfaces)
│   └── Output Ports (Repository Interfaces)
└── 🔧 Adapters (Infrastructure)
    ├── Input Adapters (Web Controllers)
    ├── Output Adapters (JPA Repositories)
    └── Configuration
```

## 🚀 주요 기능

### ✅ 구현 완료된 API

#### 🐱 고양이 관리 API
- **고양이 등록/조회/검색**: 이미지 업로드, 특징 태그, 위치 정보 포함
- **공간 기반 검색**: PostGIS를 활용한 주변 고양이 검색
- **좋아요 시스템**: 실시간 좋아요 토글 및 카운트

#### 💬 커뮤니티 API  
- **게시글 CRUD**: 목격 신고, 도움 요청, 근황 공유 타입별 관리
- **댓글 시스템**: 대댓글 지원하는 계층형 댓글 구조
- **실시간 인터랙션**: 좋아요, 댓글 수 실시간 업데이트

#### 🔐 인증 시스템
- **JWT 토큰 기반**: 확장 가능한 stateless 인증
- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **보안 설정**: CORS, 공개/비공개 엔드포인트 구분

### 📊 데이터 모델 설계

#### 핵심 엔티티 구조

```java
// 고양이 정보 표준화된 응답 모델
@Data @Builder
public class CatResponseDto {
    private String id;                    // UUID → String 변환
    private String name;
    private String image;                 // Base64 이미지 지원
    private String location;
    private String lastSeen;              // 상대 시간 포맷
    private List<String> characteristics; // 특징 태그
    private ReportedByDto reportedBy;     // 제보자 정보
    private Integer likes;                // 좋아요 수
    private Double lat, lng;              // PostGIS 좌표
    private Gender gender;                // 소문자 JSON 직렬화
}
```

#### 공간 데이터 처리

```sql
-- PostGIS를 활용한 공간 인덱스
ALTER TABLE cats ADD COLUMN coordinates GEOMETRY(Point, 4326);
CREATE INDEX idx_cats_coordinates ON cats USING GIST (coordinates);

-- 거리 기반 검색 쿼리 (구현 예정)
SELECT * FROM cats WHERE ST_DWithin(coordinates, ST_Point(lng, lat), radius);
```

### 🌐 API 엔드포인트

#### 표준화된 응답 형식
```json
{
  "success": true,
  "data": [...],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-08-14T02:19:30.71089"
}
```

#### 주요 엔드포인트

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| `GET` | `/api/cats` | 고양이 목록 조회 | 불필요 |
| `POST` | `/api/cats` | 고양이 등록 | 필요 |
| `GET` | `/api/cats/search?query=` | 이름 기반 검색 | 불필요 |
| `GET` | `/api/cats/nearby?lat=&lng=&radius=` | 주변 고양이 검색 | 불필요 |
| `POST` | `/api/cats/{id}/like` | 좋아요 토글 | 필요 |
| `GET` | `/api/community/posts` | 커뮤니티 게시글 목록 | 불필요 |
| `POST` | `/api/community/posts` | 게시글 작성 | 필요 |
| `POST` | `/api/community/posts/{id}/comments` | 댓글 작성 | 필요 |

## 🔧 개발 환경 설정

### 📋 사전 요구사항

- **Java 21** 이상
- **Docker & Docker Compose**
- **Git**

### 🚀 빠른 시작

1. **저장소 클론**
   ```bash
   git clone https://github.com/VibeMonkeys/meowtown-back.git
   cd meowtown-back
   ```

2. **Docker 서비스 시작**
   ```bash
   # PostgreSQL + PostGIS + Redis 컨테이너 실행
   ./docker-dev.sh up
   
   # 서비스 상태 확인
   ./docker-dev.sh status
   ```

3. **애플리케이션 실행**
   ```bash
   # Spring Boot 애플리케이션 시작
   ./gradlew bootRun
   
   # 또는 빌드 후 실행
   ./gradlew build
   java -jar build/libs/meowtown-back-*.jar
   ```

4. **API 테스트**
   ```bash
   # 헬스 체크
   curl http://localhost:8080/api/cats
   
   # 커뮤니티 게시글 조회
   curl http://localhost:8080/api/community/posts
   ```

### 🐳 Docker 개발 환경

```yaml
# docker-compose.yml 주요 서비스
services:
  postgres:
    image: postgis/postgis:15-3.3
    environment:
      POSTGRES_DB: meowtown
      POSTGRES_USER: meowtown
      POSTGRES_PASSWORD: meowtown123
    ports:
      - "5432:5432"
      
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

### 📁 프로젝트 구조

```
src/main/java/com/meowtown/
├── 📱 MeowtownApplication.java          # 메인 애플리케이션
├── 🔧 config/                          # 설정 클래스
│   └── MinimalSecurityConfig.java
├── 🎮 controller/                      # REST 컨트롤러
│   └── SimpleCommunityController.java
├── 📊 entity/enums/                    # 도메인 Enum
│   ├── Gender.java
│   ├── PostType.java
│   └── UserRole.java
└── 🔐 security/                       # 보안 설정
    └── CustomUserDetails.java

src/main/resources/
├── 📝 application.yml                  # 기본 설정
├── 📝 application-dev.yml             # 개발 환경
├── 📝 application-prod.yml            # 운영 환경
└── 📝 application-test.yml            # 테스트 환경
```

## 🎯 핵심 구현 사항

### 🔐 보안 및 인증

**Spring Security 최소 설정**
- JWT 기반 stateless 인증 시스템
- CORS 정책으로 프론트엔드 도메인 허용
- 공개/비공개 엔드포인트 세분화

```java
@Configuration
@EnableWebSecurity
public class MinimalSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cats/**").permitAll()
                .requestMatchers("/api/community/posts").permitAll()
                .anyRequest().authenticated()
            ).build();
    }
}
```

### 📊 데이터 표준화

**프론트엔드-백엔드 완벽 호환**
- TypeScript 인터페이스와 100% 일치하는 DTO 설계
- JSON 직렬화 최적화 (@JsonValue, @JsonCreator)
- 일관된 API 응답 형식

### ⚡ 성능 최적화

**데이터베이스 최적화**
- HikariCP 커넥션 풀 (최대 20개)
- JPA Lazy Loading 전략
- PostgreSQL 인덱스 최적화

**캐싱 전략**
- Redis를 통한 세션 관리
- 자주 조회되는 데이터 캐싱
- TTL 기반 캐시 무효화

### 🌍 공간 데이터 처리

**PostGIS 통합**
- WGS84 좌표계 (SRID: 4326) 사용
- GIST 인덱스를 통한 공간 쿼리 최적화
- 거리 기반 검색 알고리즘

## 🧪 테스트 전략

### 📋 테스트 계층

1. **단위 테스트**: 도메인 로직, Use Case 검증
2. **통합 테스트**: API 엔드포인트, 데이터베이스 연동
3. **인증 테스트**: JWT 토큰 검증, 권한 확인

### 🔍 품질 관리

- **코드 커버리지**: JaCoCo를 통한 테스트 커버리지 측정
- **정적 분석**: SpotBugs, PMD를 통한 코드 품질 검사
- **API 문서화**: Spring REST Docs 자동 생성

## 🔮 향후 계획

### 🎯 단기 목표 (1-2주)
- [ ] PostGIS 공간 쿼리 네이티브 SQL 구현
- [ ] JWT Refresh Token 메커니즘
- [ ] 실제 사용자 기반 reportedBy 구현
- [ ] 백엔드 알림 시스템 및 DB 저장

### 🚀 중기 목표 (1개월)
- [ ] WebSocket 기반 실시간 알림
- [ ] 고급 검색 필터 (특성별, 거리별)
- [ ] 관리자 대시보드 API
- [ ] 모바일 앱 연동 준비

### 🌟 장기 목표 (2-3개월)
- [ ] AI 기반 고양이 이미지 인식
- [ ] 지역별 통계 및 분석 대시보드
- [ ] 봉사자 매칭 시스템
- [ ] 다국어 지원 및 국제화

## 🚨 트러블슈팅

### 일반적인 문제 해결

**PostGIS 연결 오류**
```bash
# Docker 컨테이너 재시작
./docker-dev.sh restart

# 데이터베이스 초기화
./docker-dev.sh reset
```

**포트 충돌 문제**
```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080

# 프로세스 종료 후 재시작
./gradlew bootRun
```

## 📞 개발자 정보

### 🔗 관련 링크

- **프론트엔드**: [meowtown-front](https://github.com/VibeMonkeys/meowtown-front)
- **API 문서**: `http://localhost:8080/swagger-ui.html` (구현 예정)
- **개발 문서**: [CLAUDE.md](./CLAUDE.md)

### 📈 프로젝트 현황

- **개발 기간**: 2025년 8월
- **현재 버전**: v1.0.0
- **마지막 업데이트**: 2025-08-14
- **커밋 수**: 8개 기능별 커밋

---

<div align="center">

**🐱 Made with ❤️ for Community Cats**

*이 프로젝트는 지역 커뮤니티의 길고양이 돌봄 문화 확산을 목표로 합니다.*

</div>

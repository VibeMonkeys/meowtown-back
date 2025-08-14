# 🐱 MeowTown (우리동네 냥이도감) - 개발 문서

## 📋 프로젝트 개요
**MeowTown**은 지역 커뮤니티 기반 고양이 관리 플랫폼입니다. 사용자들이 동네 고양이들의 정보를 공유하고, 목격 정보를 등록하며, 커뮤니티 활동을 통해 고양이들을 돌볼 수 있는 웹 애플리케이션입니다.

## 🏗️ 시스템 아키텍처

### 백엔드 아키텍처: Hexagonal Architecture (Ports & Adapters)
```
📦 Backend Structure
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

### 프론트엔드 아키텍처: Feature-Sliced Design (FSD)
```
📦 Frontend Structure (src-fsd/)
├── 🎯 App (최상위 애플리케이션 설정)
├── 📄 Pages (라우트별 페이지 컴포넌트)
├── 🔧 Features (비즈니스 기능 단위)
├── 📊 Entities (도메인 엔티티)
└── 🛠️ Shared (공통 유틸리티)
```

## 🛠️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.0, Java 21
- **Database**: PostgreSQL 15 + PostGIS (공간 데이터)
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Build**: Gradle 8.11
- **Container**: Docker Compose

### Frontend  
- **Framework**: React 18, TypeScript
- **UI**: Radix UI, Tailwind CSS
- **Architecture**: Feature-Sliced Design (FSD)
- **State**: React Context/Hooks

## 🔄 프론트엔드-백엔드 표준화 (2025-08-14 완료)

### 🎯 표준화 원칙
> **"프론트엔드 기준으로 백엔드를 표준화"** - 프론트엔드의 Cat 인터페이스에 맞춰 백엔드 API 응답을 조정

### 📊 Cat 도메인 모델 표준화

#### Frontend Cat Interface (기준)
```typescript
interface Cat {
  id: string;                    // UUID → String 변환
  name: string;
  image: string;                 // primaryImageUrl → image
  location: string;
  lastSeen: string;              // lastSeenAt → lastSeen (상대시간)
  description: string;
  characteristics: string[];
  reportedBy: {                  // UUID → 객체
    name: string;
    avatar?: string;
  };
  likes: number;                 // likesCount → likes
  comments: number;
  isNeutered: boolean;
  estimatedAge: string;
  gender: 'male' | 'female' | 'unknown';  // 소문자
  lat: number;                   // latitude → lat
  lng: number;                   // longitude → lng
  reportCount: number;
}
```

#### Backend CatResponseDto (표준화 완료)
```java
@Data
@Builder
public class CatResponseDto {
    private String id;                    // UUID → String
    private String name;
    private String image;                 // primaryImageUrl → image  
    private String location;
    private String lastSeen;              // 상대 시간 포맷팅
    private String description;
    private List<String> characteristics;
    private ReportedByDto reportedBy;     // 객체 형태
    private Integer likes;                // likesCount → likes
    private Integer comments;
    private Boolean isNeutered;
    private String estimatedAge;
    private Gender gender;                // 소문자 JSON 직렬화
    private Double lat;                   // latitude → lat
    private Double lng;                   // longitude → lng  
    private Integer reportCount;
    
    // 내부용 필드들
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
```

### 🔄 Gender Enum 표준화
```java
public enum Gender {
    MALE("male"),
    FEMALE("female"), 
    UNKNOWN("unknown");
    
    @JsonValue
    public String getValue() { return value; }  // 소문자로 JSON 직렬화
}
```

### 🌐 API 엔드포인트 표준화

#### 기본 API 경로
- **Base URL**: `http://localhost:8080/api`
- **CORS**: 프론트엔드 도메인 허용
- **Authentication**: JWT (공개 엔드포인트는 permitAll)

#### Cat API 엔드포인트
```
GET    /api/cats                    # 고양이 목록 조회
GET    /api/cats/{id}               # 고양이 상세 조회  
POST   /api/cats                    # 고양이 등록
GET    /api/cats/search?query=      # 이름 기반 검색
GET    /api/cats/nearby?lat=&lng=&radius=  # 주변 고양이 조회 (프론트엔드 파라미터명)
```

#### API 응답 형식
```json
{
  "success": true,
  "data": [...],
  "message": "0마리의 고양이를 찾았습니다.",
  "timestamp": "2025-08-14T02:19:30.71089"
}
```

### ✅ 유효성 검증 규칙 (2025-08-14 수정)
```java
// 프론트엔드 사용성을 고려한 유연한 검증 규칙
@Size(min = 1, max = 50, message = "이름은 1-50자여야 합니다")
private String name;

@Size(min = 2, max = 100, message = "위치는 2-100자여야 합니다")  
private String location;

@Size(max = 500, message = "설명은 500자 이내여야 합니다")  // 최소 길이 제한 제거
private String description;  // 선택적 필드
```

## 🚀 개발 환경 설정

### 1. Docker 환경 실행
```bash
# PostgreSQL + Redis 시작
./docker-dev.sh up

# 서비스 확인
./docker-dev.sh status

# 중지
./docker-dev.sh down
```

### 2. 백엔드 실행
```bash
# 컴파일 및 실행
./gradlew bootRun

# 테스트 실행  
./gradlew test

# 빌드
./gradlew build
```

### 3. API 테스트
```bash
# 고양이 목록 조회
curl http://localhost:8080/api/cats

# 검색
curl "http://localhost:8080/api/cats/search?query=test"

# 주변 검색 (프론트엔드 파라미터)
curl "http://localhost:8080/api/cats/nearby?lat=37.5665&lng=126.9780&radius=1000"
```

## 📊 데이터베이스 구조

### 주요 엔티티
- **Cat**: 고양이 기본 정보 + PostGIS 좌표
- **User**: 사용자 정보 + JWT 인증
- **Sighting**: 고양이 목격 정보 + 위치
- **CommunityPost**: 커뮤니티 게시글
- **Comment**: 댓글 시스템
- **Like**: 좋아요 시스템

### PostGIS 공간 데이터
```sql
-- 고양이 위치 정보 (Point)
ALTER TABLE cats ADD COLUMN coordinates GEOMETRY(Point, 4326);

-- 공간 인덱스
CREATE INDEX idx_cats_coordinates ON cats USING GIST (coordinates);

-- 거리 기반 검색 (현재 비활성화 - Hibernate 6.x 호환성 이슈)
-- SELECT * FROM cats WHERE ST_DWithin(coordinates, ST_Point(lng, lat), radius);
```

## 🔐 보안 설정

### JWT 인증
- **Secret**: 환경변수로 관리 (`JWT_SECRET`)
- **만료시간**: 개발 1일, 운영 7일
- **Refresh Token**: 구현 예정

### 공개 엔드포인트
```java
.requestMatchers("/api/cats/**").permitAll()     // 고양이 정보 공개
.requestMatchers("/api/auth/**").permitAll()     // 인증 관련
.requestMatchers("/api/search/**").permitAll()   // 검색 기능
```

## 📈 성능 최적화

### 데이터베이스 최적화
- **Connection Pool**: HikariCP (최대 20개)
- **JPA 설정**: `show-sql: false` (운영), Lazy Loading
- **Redis 캐싱**: 사용자 세션, 인기 검색어

### PostGIS 성능
- **공간 인덱스**: GIST 인덱스 사용
- **거리 계산**: ST_DWithin 함수 (현재 비활성화)
- **좌표 시스템**: WGS84 (SRID: 4326)

## 🧪 테스트 전략

### 단위 테스트
- **Domain Logic**: 도메인 모델 테스트
- **Use Case**: 비즈니스 로직 테스트  
- **Repository**: 데이터 액세스 테스트

### 통합 테스트
- **API 테스트**: Controller → Service → Repository
- **인증 테스트**: JWT 토큰 검증
- **PostGIS 테스트**: 공간 쿼리 (활성화 후)

## 📝 최근 변경사항 (2025-08-14)

### 🔧 오류 수정 및 개선
1. **유효성 검증 규칙 완화** - description 최소 길이 제거, location 최소 길이 2자로 변경
2. **에러 메시지 개선** - 프론트엔드에서 상세한 에러 정보 표시
3. **reportedBy null 처리** - 인증되지 않은 사용자도 고양이 등록 가능 (익명 처리)
4. **Gender enum 대소문자 처리** - @JsonCreator로 대소문자 무관하게 처리

### 🎨 프론트엔드 신규 기능 구현 (2025-08-14)
1. **고양이 상세 페이지 구현**
   - 새 컴포넌트: `CatDetail.tsx`
   - 고양이 정보, 목격 이력, 위치, 관리 옵션 표시
   - 도감/홈/검색에서 고양이 클릭 시 상세 페이지로 이동

2. **알림 시스템 구현**
   - 새 컴포넌트: `NotificationPanel.tsx`
   - 알림 타입: 고양이 등록, 좋아요, 댓글, 목격, 시스템, 경고
   - 읽음/안읽음 필터링, 개별/전체 삭제 기능
   - 고양이 등록/좋아요/댓글 시 실시간 알림 생성

3. **지도 기능 개선**
   - 고양이 마커 클릭 시 정보 표시
   - "상세 정보 보기" 버튼으로 상세 페이지 이동
   - 카카오맵 길찾기 연동

4. **인터랙션 기능 구현**
   - 좋아요: 카운트 증가 + 알림 생성
   - 댓글: 사용자 입력 + 카운트 증가 + 알림 생성
   - 공유: Web Share API 또는 클립보드 복사
   - 목격 신고, 사진 추가, 정보 수정 제안 등 관리 기능

## 📝 이전 변경사항

### ✅ 완료된 표준화 작업
1. **CatResponseDto 프론트엔드 기준 수정** - 필드명, 타입 완전 일치
2. **Gender enum 소문자 변경** - JSON 응답 소문자로 통일
3. **API 엔드포인트 표준화** - 프론트엔드 파라미터명 적용
4. **유효성 검증 통일** - 프론트엔드와 동일한 규칙
5. **보안 설정 수정** - 경로 매칭 문제 해결
6. **서버 설정 최적화** - 컨텍스트 패스 정리

### 🔄 현재 상태
- **백엔드**: ✅ 실행 중 (http://localhost:8080)
- **데이터베이스**: ✅ PostgreSQL + PostGIS 연결
- **캐시**: ✅ Redis 연결
- **API 테스트**: ✅ 모든 엔드포인트 정상 응답
- **프론트엔드 호환성**: ✅ 100% 호환

## 🔮 향후 계획

### 단기 (1-2주)
- [ ] PostGIS 공간 쿼리 재활성화 (네이티브 SQL 방식)
- [ ] 실제 사용자 조회로 reportedBy 구현
- [x] 좋아요/댓글 카운트 실제 구현 (프론트엔드 완료)
- [x] 이미지 업로드 기능 구현 (Base64 방식 적용)
- [ ] 실제 지도 API 연동 (Google Maps 또는 Kakao Map)
- [ ] 백엔드 알림 시스템 구현 및 DB 저장

### 중기 (1개월)
- [ ] 실시간 알림 시스템 (WebSocket)
- [ ] 고급 검색 필터 (특성별, 거리별)
- [ ] 관리자 대시보드
- [ ] 모바일 앱 연동 준비

### 장기 (2-3개월)  
- [ ] AI 기반 고양이 인식
- [ ] 지역별 통계 및 분석
- [ ] 봉사자 매칭 시스템
- [ ] 다국어 지원

## 🚨 트러블슈팅

### PostGIS 쿼리 이슈
```
문제: "Non-boolean expression used in predicate context: ST_DWithin"
해결: 공간 쿼리 일시 비활성화, 향후 네이티브 SQL로 구현 예정
```

### 보안 설정 이슈  
```
문제: 403 Forbidden - 경로 매칭 실패
해결: WebConfig 경로 프리픽스 제거, 컨트롤러에 전체 경로 적용
```

### 컴파일 오류
```
문제: CatImageRepository 타입 불일치
해결: findByUploadedBy(UUID) → findByUploadedById(UUID)
```

## 🚀 주요 기능 목록

### 구현 완료된 기능
- ✅ 고양이 등록 및 조회 (이미지 포함)
- ✅ 고양이 검색 (이름 기반)
- ✅ 고양이 상세 정보 페이지
- ✅ 지도 기반 위치 표시
- ✅ 커뮤니티 게시글 (좋아요/댓글)
- ✅ 알림 시스템 (프론트엔드)
- ✅ 네비게이션 확인 다이얼로그
- ✅ 이미지 리사이징 및 Base64 업로드
- ✅ 좋아요/댓글/공유 기능
- ✅ 목격 신고 및 관리 기능

### 개발 중인 기능
- 🔧 실제 지도 API 연동
- 🔧 사용자 인증 시스템
- 🔧 백엔드 알림 저장
- 🔧 실시간 업데이트 (WebSocket)

---

## 📞 지원 및 문의

이 문서는 MeowTown 프로젝트의 최신 상태를 반영합니다.  
마지막 업데이트: **2025-08-14**

**개발 환경 실행 명령어 요약:**
```bash
# Docker 서비스 시작
./docker-dev.sh up

# 백엔드 실행
./gradlew bootRun

# API 테스트
curl http://localhost:8080/api/cats
```
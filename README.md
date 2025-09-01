# FridgeMate API

냉장고 재고관리 + AI 레시피 추천 시스템의 백엔드 API

## 📋 프로젝트 개요

**MVP 목표 (2주 이내 구현):**
- 냉장고 재고 CRUD
- 유통기한 임박 정렬/필터
- 현재 재고로 만들 수 있는 간단 레시피 추천 (규칙기반 + LLM 옵션)

**주요 특징:**
- 단일 사용자 기준 (로그인 미구현)
- 실시간 재고 추적 및 소비 관리
- 유통기한 기반 우선순위 정렬
- AI 기반 맞춤형 레시피 추천

## 🛠 기술 스택

### Backend
- **Framework:** Spring Boot 3.3.5
- **Language:** Java 21
- **Database:** PostgreSQL 15
- **Migration:** Flyway
- **Documentation:** OpenAPI 3 (Springdoc)
- **Build Tool:** Maven

### Libraries
- Spring Web, Spring Data JPA, Validation
- Lombok (코드 간소화)
- Jackson (JSON 처리)

### DevOps
- **Container:** Docker & Docker Compose
- **Database:** PostgreSQL (Docker)

## 🚀 빠른 시작

### 1. 사전 요구사항
- Java 21
- Docker & Docker Compose
- Maven 3.9+

### 2. 프로젝트 클론
```bash
git clone <repository-url>
cd fridgemate-api
```

### 3. 환경 설정
```bash
# application.yml에서 환경별 설정 확인
# 개발 환경은 기본적으로 localhost:5432 PostgreSQL 사용
```

### 4. 데이터베이스 실행
```bash
# PostgreSQL 컨테이너 실행
docker compose up -d db

# 또는 전체 스택 실행 (DB + API)
docker compose up
```

### 5. 애플리케이션 실행
```bash
# 로컬 개발 모드
./mvnw spring-boot:run

# 또는 JAR 빌드 후 실행
./mvnw clean package
java -jar target/fridgemate-api-0.0.1-SNAPSHOT.jar
```

### 6. API 문서 확인
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## 📊 데이터베이스 스키마

### Items 테이블
```sql
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity NUMERIC(15,3) NOT NULL DEFAULT 0,
    unit VARCHAR(20) NOT NULL,
    expiry_date DATE,
    category VARCHAR(30),
    location VARCHAR(20),
    memo TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

## 🔗 API 엔드포인트

### 재고 관리
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/items` | 재고 목록 조회 (검색/필터/정렬) |
| GET | `/api/items/{id}` | 재고 단건 조회 |
| POST | `/api/items` | 재고 생성 |
| PUT | `/api/items/{id}` | 재고 수정 |
| DELETE | `/api/items/{id}` | 재고 삭제 |
| POST | `/api/items/{id}/consume` | 재고 소비 (수량 감소) |
| GET | `/api/items/expiring` | 유통기한 임박 상품 조회 |

### 레시피 추천
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/recipes/suggest` | 레시피 추천 |

### 기타
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | 헬스 체크 |

## 📝 API 사용 예시

### 재고 생성
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "계란",
    "quantity": 10,
    "unit": "개",
    "expiryDate": "2024-12-31",
    "category": "유제품",
    "location": "냉장",
    "memo": "신선한 계란"
  }'
```

### 재고 소비
```bash
curl -X POST http://localhost:8080/api/items/1/consume \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 3
  }'
```

### 레시피 추천
```bash
curl -X POST http://localhost:8080/api/recipes/suggest \
  -H "Content-Type: application/json" \
  -d '{
    "servings": 2,
    "diet": "일반"
  }'
```

### 검색 및 필터링
```bash
# 유통기한 임박 상품 (3일 이내)
curl "http://localhost:8080/api/items?expiring=3&sortBy=expiryDate&sortDir=asc"

# 카테고리별 검색
curl "http://localhost:8080/api/items?category=유제품"

# 키워드 검색
curl "http://localhost:8080/api/items?keyword=계란"
```

## 🤖 AI 레시피 추천 시스템

### 규칙 기반 추천 (기본)
- **데이터:** `src/main/resources/recipes.json`
- **알고리즘:** 재료 일치율(70%) + 신선도 가중치(30%)
- **출력:** 상위 3-5개 레시피 추천

### LLM 기반 추천 (옵션)
```yaml
# application.yml 설정
ai:
  provider: llm  # rule | llm
  llm:
    api-key: ${LLM_API_KEY}
    base-url: ${LLM_BASE_URL}
```

환경변수로 LLM 설정:
```bash
export LLM_API_KEY="your-api-key"
export LLM_BASE_URL="https://api.openai.com"
```

## 🧪 테스트

### 단위 테스트 실행
```bash
./mvnw test
```

### 통합 테스트 실행
```bash
./mvnw test -Dtest="*IntegrationTest"
```

### 테스트 커버리지
```bash
./mvnw jacoco:report
```

## 🗂 프로젝트 구조
```
src/
├── main/java/com/fridgemate/api/
│   ├── controller/          # REST 컨트롤러
│   ├── service/            # 비즈니스 로직
│   ├── repository/         # 데이터 접근 계층
│   ├── domain/             # 엔티티
│   ├── dto/                # 데이터 전송 객체
│   ├── exception/          # 예외 처리
│   └── config/             # 설정 클래스
├── main/resources/
│   ├── db/migration/       # Flyway 마이그레이션
│   ├── recipes.json        # 레시피 데이터
│   └── application.yml     # 애플리케이션 설정
└── test/                   # 테스트 코드
```

## 🚀 배포

### Docker Compose 배포
```bash
# 전체 스택 실행 (DB + API)
docker compose up -d

# 로그 확인
docker compose logs -f app

# 중지
docker compose down
```

### 운영 환경 설정
```yaml
# application.yml의 docker 프로파일 활성화
spring:
  profiles:
    active: docker
```

## 🔧 MySQL 대체 설정

PostgreSQL 대신 MySQL 사용 시:

1. **pom.xml** 의존성 변경:
```xml
<!-- PostgreSQL 주석 처리 -->
<!--
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
-->

<!-- MySQL 활성화 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. **docker-compose.yml** 서비스 변경:
```yaml
db:
  image: mysql:8.0
  environment:
    MYSQL_DATABASE: fridgemate
    MYSQL_USER: fridgemate_user
    MYSQL_PASSWORD: fridgemate_pass
    MYSQL_ROOT_PASSWORD: root_password
```

3. **application.yml** 프로파일 활성화:
```yaml
spring:
  profiles:
    active: mysql
```

## 📚 추가 문서

- **API 문서:** http://localhost:8080/swagger-ui.html
- **Actuator:** http://localhost:8080/actuator
- **헬스 체크:** http://localhost:8080/actuator/health

## 🤝 기여 방법

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 라이선스

MIT License - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 🏷 버전 히스토리

- **v1.0.0** - MVP 기능 구현
  - 재고 CRUD
  - 유통기한 관리
  - 규칙 기반 레시피 추천
  - Docker 배포 지원

## 📞 문의

- **Email:** contact@fridgemate.com
- **GitHub Issues:** [이슈 등록](../../issues)

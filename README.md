# QuickURL

> **🌐 배포 주소**: [https://quickurl.haisia.dev/](https://quickurl.haisia.dev/)

> **👨‍💻 Project by 최준혁** - 기획, 설계, 개발, 배포 100% 개인 프로젝트  
> GitHub: [@Haisia](https://github.com/Haisia) | Blog: [Velog](https://velog.io/@haisia)

## 📌 프로젝트 소개

QuickURL은 Kotlin과 Spring Boot 기반의 고성능 URL 단축 서비스입니다.

긴 URL을 짧고 간편한 형태로 변환하여 공유하고 관리할 수 있는 웹 애플리케이션으로,
개인 사용자부터 기업까지 누구나 쉽게 링크를 단축하고 추적할 수 있도록 설계되었습니다.

### 주요 기능

**URL 단축 및 관리**

- 짧은 URL 생성 및 리다이렉트
- QR 코드 자동 생성
- URL 만료 정책 관리 (마지막 사용일 기준 90일 자동 만료)
- 커스텀 만료 기간 설정 (1일, 7일, 30일, 90일, 기본 정책)

**사용자 관리**

- 회원가입 및 로그인 (JWT 기반 인증)
- 개인 대시보드를 통한 생성된 URL 관리
- URL별 클릭 수, 생성일, 최근 사용일, 만료일 추적
- URL 복사, 삭제 기능

**통계 및 모니터링**

- 실시간 전체 사용자 클릭 수 집계
- 일별/누적 클릭 통계 제공

**보안 및 안정성**

- XSS 등 악성 요청 방지
- Rate Limiting (1초당 10회 제한)
- Spring Security 기반 인증/인가

**성능 최적화**

- Redis 캐싱으로 빠른 URL 조회
- Spring Event 기반 비동기 처리 (로깅, 알림)
- k6 부하 테스트를 통한 점진적 성능 개선

**알림**

- 회원가입 및 URL 생성 시 이메일 발송

---

## 🛠️ 기술 스택

### Backend

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Gradle (Kotlin DSL)
- **JVM**: Java 21

### Core Dependencies

- **Spring Data JPA**: 데이터베이스 ORM 및 영속성 관리
- **Spring Security**: 인증/인가 및 보안
- **Spring Data Redis**: 캐싱 및 세션 관리
- **Spring WebFlux**: 비동기 논블로킹 처리 (Reactor)
- **Thymeleaf**: 서버 사이드 템플릿 엔진

### Database

- **MariaDB**: 메인 데이터베이스
- **Redis**: 캐싱 및 성능 최적화
- **H2**: 테스트 환경용 인메모리 DB

### Security & Authentication

- **JWT** (jjwt 0.13.0): 토큰 기반 인증
- **Spring Security**: 보안 및 인증/인가 처리

### Libraries

- **ZXing** (3.5.3): QR 코드 생성
- **SpringDoc OpenAPI** (2.8.13): API 문서 자동화
- **Jackson Kotlin Module**: JSON 직렬화/역직렬화
- **Spring Retry**: 재시도 메커니즘

### DevOps & Infrastructure

- **Docker & Docker Compose**: 컨테이너화 및 로컬 개발 환경
- **GitHub Actions**: CI/CD 파이프라인
- **Claude API**: AI 기반 자동 코드 리뷰

### Testing & Performance

- **JUnit 5**: 단위 테스트 프레임워크
- **MockK** (1.13.8): Kotlin 전용 모킹 라이브러리
- **k6**: 부하 테스트 및 성능 측정

### Monitoring

- **Spring Boot Actuator**: 애플리케이션 상태 모니터링

---

## 🏗️ 프로젝트 구조

### 아키텍처 개요

QuickURL은 **헥사고날 아키텍처(Hexagonal Architecture)**와 **EDA(Event-Driven Architecture)**를 기반으로 설계되었습니다.

계층 간 의존성은 **단방향**으로 엄격하게 관리되며, 비즈니스 로직의 독립성과 테스트 용이성을 보장합니다.

### 계층 구조

```
src/main/kotlin/dev/haisia/quickurl/
├── domain/              # 핵심 비즈니스 로직 및 도메인 모델
│   ├── url/            # URL 도메인
│   ├── user/           # 사용자 도메인
│   ├── Email.kt        # 이메일 VO
│   ├── Password.kt     # 비밀번호 VO
│   └── Duration.kt     # 만료 기간 VO
│
├── application/         # 유스케이스 및 애플리케이션 서비스
│   ├── url/            # URL 관련 유스케이스
│   ├── user/           # 사용자 관련 유스케이스
│   └── shared/         # 공유 애플리케이션 로직
│
└── adapter/            # 외부 인터페이스 어댑터
    ├── web/            # 웹 인터페이스
    │   ├── api/        # REST API 컨트롤러
    │   └── page/       # Thymeleaf 페이지 컨트롤러
    ├── persistence/    # 데이터베이스 어댑터
    ├── cache/          # Redis 캐시 어댑터
    ├── email/          # 이메일 발송 어댑터
    ├── qrcode/         # QR 코드 생성 어댑터
    ├── security/       # 보안 설정
    ├── filter/         # 필터 (Rate Limit, CSRF 등)
    ├── scheduler/      # 스케줄러 (만료 URL 정리)
    └── exceptionhandler/ # 글로벌 예외 처리
```

### 의존성 규칙

의존성의 **방향**은 엄격하게 지켜지며, 의존성의 **단계**는 유연합니다.

```
✅ 허용되는 의존성 방향:
adapter → application → domain
adapter → domain (직접 참조 가능)

❌ 금지되는 의존성 방향:
domain → application
domain → adapter
application → adapter
```

이를 통해 도메인 로직이 외부 기술에 의존하지 않고 독립적으로 유지됩니다.

### 핵심 설계 원칙

#### 1. Value Object (VO) 기반 캡슐화

도메인의 각 책임을 VO로 캡슐화하여 값 검증 및 불변성을 보장합니다.

```kotlin
// Email.kt - 이메일 검증 로직 캡슐화
data class Email(val value: String) {

   companion object {
      private val EMAIL_REGEX =
         "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}$".toRegex()
   }
   init {
      require(value.matches(EMAIL_REGEX)) {
         "Invalid email format: $value"
      }
   }
}

// Password.kt - 비밀번호 정책 캡슐화
data class Password(val value: String) {

   companion object {
      private val PASSWORD_REGEX = "^\\S{6,}$".toRegex()
   }

   init {
      require(value.matches(PASSWORD_REGEX)) {
         "Password must be at least 6 characters long (no spaces allowed)"
      }
   }
}

```

#### 2. 계층별 커스텀 예외 처리

각 계층은 고유한 예외를 정의하고, 글로벌 ExceptionHandler가 이를 일관된 형태로 변환합니다.

```kotlin
class ShortKeyGenerationException(
   message: String = "Id must not be null. Please save url before generating short key."
) : DomainException(message)

class ShortKeyNotGeneratedException(
   message: String = "Short key has not been generated yet. Call generateShortKey() first."
) : DomainException(message)
```

모든 예외는 HTTP 상태 코드와 메시지가 명확히 정의되어 **일관되고 정확한 응답**을 보장합니다.

#### 3. 공통 응답 DTO

모든 API 응답은 `ApiResponse` 클래스를 통해 표준화됩니다.

```kotlin
// 성공 응답
ApiResponse.ok(data)              // 200 OK
ApiResponse.created(data)         // 201 Created
ApiResponse.noContent()           // 204 No Content

// 에러 응답
ApiResponse.badRequest(message)   // 400 Bad Request
ApiResponse.notFound(message)     // 404 Not Found
ApiResponse.unauthorized(message) // 401 Unauthorized
ApiResponse.tooManyRequests(msg)  // 429 Too Many Requests
```

페이징 데이터는 `ApiPageableData`로 표준화됩니다.

```kotlin
data class ApiPageableData<T>(
  val totalPages: Int,
  val totalCount: Long,
  val items: List<T>
)
```

#### 4. API와 페이지 계층 분리

Thymeleaf를 사용하므로 웹 계층을 **API**와 **Page**로 철저히 분리합니다.

```
adapter/web/
├── api/         # REST API 엔드포인트 (JSON 응답)
│   ├── UrlController.kt
│   └── AuthController.kt
└── page/        # Thymeleaf 페이지 컨트롤러 (HTML 응답)
    ├── MainPageController.kt
    └── AuthPageController.kt
```

**보안**: 필터와 Referer 헤더 검증을 통해 **해당 서버의 페이지에서만 API 호출이 가능**하도록 제한합니다.

#### 5. Event-Driven Architecture (EDA)

사용자 응답에 영향을 미치지 않는 로직은 Spring Event를 활용하여 비동기로 처리합니다.

```kotlin
// 예시: URL 생성 시 이메일 발송
eventPublisher.publishEvent(UrlEvent.UrlCreated(saved.getIdOrThrow()))

// 비동기 이벤트 리스너
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
fun handleUrlCreated(event: UrlEvent.UrlCreated) {
   val url = urlRepository.findById(event.urlId).getOrNull() ?: return

   if(url.createdBy != "anonymous") {
      val user = userRepository.findById(UUID.fromString(url.createdBy)).getOrNull() ?: return

      mailSender.sendUrlCreated(
         recipientEmail = user.email.value,
         recipientName = user.email.value,
         shortKey = url.getShortKeyOrThrow(),
         originalUrl = url.originalUrl,
      ).subscribe()
   }
}
```

로깅, 통계 집계, 알림 발송 등이 이벤트를 통해 비동기로 처리되어 응답 속도를 보장합니다.

### 주요 설계 결정사항

#### 도메인 모델과 JPA 엔티티 통합

헥사고날 아키텍처에서 도메인 모델과 영속성 모델(JPA 엔티티)을 분리하는 경우도 있지만,
이 프로젝트에서는 **도메인 모델을 JPA 엔티티로 직접 사용**합니다.

**설계 근거**

1. **어노테이션의 본질적 특성**
    - JPA 어노테이션(`@Entity`, `@Id` 등)은 런타임 메타데이터이며 비즈니스 로직에 영향을 주지 않음
    - 도메인 로직의 순수성은 메서드와 비즈니스 규칙으로 보장되며, 어노테이션은 기술적 메타데이터일 뿐

2. **매핑 오버헤드 제거**
    - 도메인-엔티티 분리 시 양방향 변환 로직이 계층마다 반복됨
    - 의미 없는 보일러플레이트 코드 증가 및 유지보수 비용 상승

3. **JPA 기능 활용**
    - 더티 체킹(Dirty Checking)을 통한 자동 변경 감지
    - 지연 로딩, 영속성 컨텍스트 등 JPA의 강력한 기능 직접 활용

4. **실용주의적 접근**
    - 여러 사이드 프로젝트에서 분리 방식을 시도했으나 생산성 저하 경험
    - 이론적 순수성보다 실제 개발 효율성과 유지보수성을 우선

**트레이드오프 인식**

이 결정은 JPA에 대한 의존성을 도메인 계층에 포함시키지만, 다음과 같은 이유로 수용 가능하다고 판단했습니다:

- 영속성 기술 변경 시 마이그레이션 비용은 있으나, 실무에서 ORM 교체는 극히 드묾
- 비즈니스 로직과 영속성 로직의 **책임 분리**는 여전히 명확히 유지됨
- 프로젝트 규모와 팀 상황에 맞는 **현실적인 절충안**

#### 브라우저 캐시를 사용하지 않는 리다이렉트

URL 클릭 시 리다이렉트 처리에서 **브라우저 캐시를 의도적으로 비활성화**했습니다.

**변경 내역**

```kotlin
// 기존 방식 (브라우저 캐시 사용)
return ResponseEntity
  .status(HttpStatus.MOVED_PERMANENTLY)  // 301
  .location(URI.create(url))
  .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
  .build()

// 현재 방식 (브라우저 캐시 비활성화)
return ResponseEntity
  .status(HttpStatus.FOUND)  // 302
  .location(URI.create(url))
  .cacheControl(CacheControl.noStore())
  .build()
```

**설계 근거**

1. **통계 정확성 우선**
    - 클릭 수는 마케팅 성과, 광고 효과 측정 등 **실적과 직결된 핵심 지표**
    - 브라우저 캐시로 인한 클릭 누락은 비즈니스 의사결정에 치명적

2. **HTTP 상태 코드 변경**
    - `301 MOVED_PERMANENTLY`: 영구 이동으로 브라우저가 적극적으로 캐싱
    - `302 FOUND`: 임시 리다이렉트로 매번 서버에 요청

3. **Cache-Control 명시**
    - `noStore()`: 브라우저와 중간 프록시 모두에서 캐시 금지
    - 모든 클릭이 서버로 전달되어 정확한 통계 집계 가능

**트레이드오프 고려**

이 결정은 다음과 같은 트레이드오프를 수반합니다:

| 항목      | 캐시 사용 (301)      | 캐시 미사용 (302)        |
|---------|------------------|---------------------|
| 통계 정확성  | ❌ 낮음 (캐시로 인한 누락) | ✅ 높음 (모든 클릭 집계)     |
| 서버 부하   | ✅ 낮음             | ⚠️ 높음               |
| 응답 속도   | ✅ 빠름 (캐시)        | ⚠️ 약간 느림 (매번 서버 요청) |
| 비즈니스 가치 | ❌ 부정확한 지표        | ✅ 정확한 실적 측정         |

**결론**: 통계 정확성이 비즈니스에 미치는 영향이 크므로 캐시 비활성화를 선택했으며,
Redis 캐싱과 성능 최적화로 서버 부하를 보완했습니다.

향후 **사내 정책이나 서비스 특성에 따라 유연하게 조정 가능**하도록 설계되었습니다.

---

## 🚀 시작하기

### 사전 요구사항

다음 항목들이 설치되어 있어야 합니다:

- **JDK 21** 이상
- **Docker Desktop** (MariaDB, Redis 컨테이너 실행용)
- **Git**

> **중요**: 프로젝트는 `spring-boot-docker-compose` 의존성을 사용하여 애플리케이션 시작 시 자동으로 필요한 컨테이너(MariaDB, Redis)를 실행합니다. 따라서 **Docker가
실행 중**이어야 합니다.

### 설치 및 실행

#### 1. Docker 실행 확인

애플리케이션을 실행하기 전에 Docker Desktop이 실행 중인지 확인하세요.

```bash
docker --version
```

#### 2. 프로젝트 클론

```bash
git clone https://github.com/Haisia/quickurl.git
cd quickurl
```

#### 3. 애플리케이션 실행

**Linux / macOS**

```bash
./gradlew bootRun
```

**Windows (PowerShell / CMD)**

```cmd
gradlew.bat bootRun
```

또는

```powershell
.\gradlew.bat bootRun
```

#### 4. 접속 확인

애플리케이션이 정상적으로 실행되면 다음 주소로 접속할 수 있습니다:

- **메인 페이지**: http://localhost:8080
- **API 문서 (Swagger)**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

### 환경 변수

기본 환경 변수는 이미 설정되어 있어 별도의 설정 없이 바로 실행 가능합니다.

필요시 `.env` 파일을 생성하여 다음과 같은 항목을 커스터마이징할 수 있습니다:

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/mydatabase
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=secret
# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=3600000
# Mail (선택사항 - Brevo SMTP)
SMTP_BREVO_API_KEY=your-brevo-api-key
SMTP_BREVO_BASE_URL=https://api.brevo.com
```

### 컨테이너 직접 관리 (선택사항)

Spring Boot Docker Compose 대신 수동으로 컨테이너를 관리하려면:

```bash
# 컨테이너 시작
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun  # Linux/Mac
gradlew.bat bootRun  # Windows

# 컨테이너 종료
docker-compose down
```

---

## 🔄 CI/CD 파이프라인

QuickURL은 GitHub Actions를 활용한 자동화된 CI/CD 파이프라인을 구축하여 코드 품질 관리와 배포 프로세스를 자동화했습니다.

### 파이프라인 개요

```
개발 흐름:
dev 브랜치 → Pull Request → AI 코드 리뷰 → 병합
                                ↓
                             master 브랜치 → Docker 빌드 → 배포
```

### 1. AI 기반 자동 코드 리뷰 (dev 브랜치)

**트리거 조건**

- `dev` 브랜치로의 Pull Request 생성 또는 업데이트

**워크플로우**

```yaml
# .github/workflows/code-review.yaml
name: Code Review

on:
  pull_request:
    types: [ opened, synchronize ]
    branches:
      - dev
```

**동작 과정**

1. **변경 사항 감지**
    - Pull Request에서 수정된 파일 목록과 diff 추출

2. **Claude API 호출**
    - Anthropic Claude API에 변경 내용 전송
    - 코드 품질, 아키텍처 준수, 잠재적 버그 등을 AI가 분석

3. **리뷰 코멘트 생성**
    - AI 분석 결과를 Pull Request에 자동으로 코멘트 작성
    - 개선 제안, 보안 이슈, 코드 스타일 등 포괄적 리뷰 제공

**기술 스택**

- Python 3.x (리뷰 스크립트)
- PyGithub (GitHub API 연동)
- Claude API (코드 분석)

**장점**

- 24/7 즉각적인 코드 리뷰
- 일관된 코드 품질 기준 적용
- 개발자의 리뷰 부담 경감
- 아키텍처 원칙 자동 검증

### 2. 자동 배포 파이프라인 (master 브랜치)

**트리거 조건**

- `master` 또는 `main` 브랜치로의 Push 이벤트

**워크플로우**

```yaml
# .github/workflows/deploy.yml
name: CI/CD Pipeline

on:
  push:
    branches:
      - main
      - master
```

**동작 과정**

**Job 1: build-and-test**

```bash
# 1. 코드 체크아웃
# 2. JDK 21 설정
# 3. Gradle 빌드 및 테스트 실행
./gradlew clean build -x test
./gradlew test
```

**Job 2: build-docker-image** (테스트 통과 후)

```bash
# 1. Docker 이미지 빌드
docker build -t quickurl:latest .

# 2. GitHub Container Registry 푸시
docker tag quickurl:latest ghcr.io/username/quickurl:latest
docker push ghcr.io/username/quickurl:latest
```

- Multi-stage 빌드로 이미지 최적화
- ARM64 플랫폼 타겟 (EC2 Graviton)
- 브랜치명, SHA, latest 등 다중 태깅

**Job 3: deploy** (이미지 푸시 완료 후)

GitHub Actions에서 직접 EC2 서버에 SSH 접속하여 배포를 수행합니다.

```bash
# 1. 환경 변수 파일(.env) 생성
# 2. EC2로 파일 전송 (SCP)
#    - docker-compose.prod.yml
#    - .env

# 3. EC2에 SSH 접속 후 배포 명령 실행
cd /home/ubuntu/quickurl

# 최신 이미지 pull
docker compose -f docker-compose.prod.yml pull

# 기존 컨테이너 중지 및 제거
docker compose -f docker-compose.prod.yml down

# 새 컨테이너 시작
docker compose -f docker-compose.prod.yml up -d

# 사용하지 않는 이미지 정리
docker image prune -af

# 로그 및 상태 확인
docker compose -f docker-compose.prod.yml logs --tail=50
docker compose -f docker-compose.prod.yml ps
```

**배포 환경**

- AWS EC2 (Ubuntu, Graviton/ARM64)
- Docker Compose (MariaDB, Redis, Application)
- GitHub Actions SSH/SCP 액션 활용

**보안**

- GitHub Secrets로 민감 정보 관리
    - `EC2_HOST`, `EC2_USERNAME`, `EC2_SSH_KEY`
    - `DB_PASSWORD`, `JWT_SECRET`, `SMTP_BREVO_API_KEY` 등
- Private SSH Key를 통한 안전한 접속

### CI/CD 모니터링

**배포 상태 확인**

- GitHub Actions 대시보드에서 실시간 모니터링
- 실패 시 이메일 알림

**롤백 전략**

- 이전 Docker 이미지 태그로 즉시 롤백 가능
- `docker-compose up -d --force-recreate`

### 환경별 배포 전략

| 환경          | 브랜치      | 배포 방식 | 용도        |
|-------------|----------|-------|-----------|
| Development | `dev`    | 수동 배포 | 개발 및 테스트  |
| Production  | `master` | 자동 배포 | 실제 서비스 운영 |

---

## 📚 API 문서

QuickURL은 **SpringDoc OpenAPI**를 사용하여 API 문서를 자동 생성합니다.

API 명세는 인터페이스 기반으로 정의되어 있으며, 컨트롤러가 해당 인터페이스를 구현하는 구조로 설계되었습니다.

### Swagger UI 접속

- **로컬**: http://localhost:8080/swagger-ui.html
- **프로덕션**: https://quickurl.haisia.dev/swagger-ui.html

### API 개요

QuickURL의 REST API는 크게 3가지 카테고리로 구성됩니다.

#### 1. 인증 API (`/api/auth`)

사용자 인증 및 계정 관리 관련 API

| 엔드포인트                | 메서드  | 설명           | 인증 필요 |
|----------------------|------|--------------|-------|
| `/api/auth/register` | POST | 회원가입         | ❌     |
| `/api/auth/login`    | POST | 로그인          | ❌     |
| `/api/auth/logout`   | POST | 로그아웃         | ✅     |
| `/api/auth/me`       | GET  | 현재 사용자 정보 조회 | ✅     |
| `/api/auth/refresh`  | POST | 액세스 토큰 갱신    | ✅     |

**주요 특징**

- JWT 기반 토큰 인증
- Access Token (15분), Refresh Token (7일) 사용
- HttpOnly 쿠키를 통한 토큰 관리

#### 2. URL 단축 API (`/api/urls`)

URL 생성, 관리, QR 코드 생성 관련 API

| 엔드포인트                         | 메서드    | 설명               | 인증 필요 |
|-------------------------------|--------|------------------|-------|
| `/api/urls`                   | POST   | 단축 URL 생성        | ❌     |
| `/api/urls/{shortKey}`        | DELETE | 단축 URL 삭제        | ✅     |
| `/api/urls/my`                | GET    | 내가 생성한 URL 목록 조회 | ✅     |
| `/api/urls/{shortKey}/qrcode` | GET    | QR 코드 생성         | ❌     |

**주요 특징**

- 비회원도 URL 생성 가능 (단, 관리는 불가)
- 회원은 만료 기간 설정 가능 (1일, 7일, 30일, 90일, 기본정책)
- 페이지네이션 지원 (내 URL 목록)
- QR 코드 크기 커스터마이징 가능

#### 3. 클릭 통계 API (`/api/click-logs`)

URL 클릭 통계 조회 관련 API

| 엔드포인트                              | 메서드 | 설명             | 인증 필요 |
|------------------------------------|-----|----------------|-------|
| `/api/click-logs/{shortKey}/stats` | GET | 단축 URL 클릭 수 조회 | ❌     |
| `/api/click-logs/global-stats`     | GET | 전체 클릭 통계 조회    | ❌     |

**주요 특징**

- 실시간 클릭 수 집계
- 일일 클릭 수 / 누적 클릭 수 제공
- Redis 캐싱으로 빠른 응답

### API 응답 형식

모든 API는 일관된 응답 구조를 사용합니다.

**성공 응답**

```json
{
  "data": {
    // 실제 데이터
  }
}
```

**에러 응답**

```json
{
  "data": {
    "message": "에러 메시지"
  }
}
```

**페이징 응답**

```json
{
  "data": {
    "total_pages": 10,
    "total_count": 95,
    "items": [
      // 아이템 목록
    ]
  }
}
```

### 주요 API 상세

#### POST /api/auth/register

회원가입

**Request Body**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (201 Created)**

```json
{
  "data": {
    "user_id": 1,
    "email": "user@example.com"
  }
}
```

**Error Codes**

- `400`: 잘못된 요청 (이메일 형식 오류, 비밀번호 규칙 위반)
- `409`: 이미 존재하는 이메일

---

#### POST /api/urls

단축 URL 생성

**Request Body**

```json
{
  "original_url": "https://example.com/very/long/url",
  "custom_duration": "SEVEN_DAYS"
  // 선택사항 (로그인 시에만)
}
```

**Response (201 Created)**

```json
{
  "data": {
    "short_key": "abc123",
    "short_url": "https://quickurl.haisia.dev/abc123",
    "original_url": "https://example.com/very/long/url",
    "expires_at": "2025-11-07T10:00:00"
  }
}
```

**Duration Options**

- `ONE_DAY`: 1일
- `SEVEN_DAYS`: 7일
- `THIRTY_DAYS`: 30일
- `NINETY_DAYS`: 90일
- `DEFAULT`: 기본 정책 (마지막 사용일 기준 90일)

---

#### GET /api/urls/my

내가 생성한 URL 목록 조회 (페이지네이션)

**Query Parameters**

- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 기준 (예: `createdAt,desc`)

**Response (200 OK)**

```json
{
  "data": {
    "total_pages": 5,
    "total_count": 95,
    "items": [
      {
        "short_key": "abc123",
        "short_url": "https://quickurl.haisia.dev/abc123",
        "original_url": "https://example.com/...",
        "click_count": 42,
        "created_at": "2025-10-01T10:00:00",
        "last_accessed_at": "2025-10-30T15:30:00",
        "expires_at": "2025-12-30T10:00:00"
      }
    ]
  }
}
```

---

#### GET /api/urls/{shortKey}/qrcode

QR 코드 생성

**Path Parameters**

- `shortKey`: 단축 URL 키 (예: abc123)

**Query Parameters**

- `size`: QR 코드 이미지 크기 (픽셀, 기본값: 200)

**Response (200 OK)**

```json
{
  "data": {
    "qr_code_base64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "short_url": "https://quickurl.haisia.dev/abc123"
  }
}
```

---

#### GET /api/click-logs/global-stats

전체 클릭 통계 조회

**Response (200 OK)**

```json
{
  "data": {
    "today_clicks": 1234,
    "total_clicks": 98765
  }
}
```

### API 보안

**1. Rate Limiting**

- 모든 API는 1초당 10회로 제한
- 초과 시 `429 Too Many Requests` 응답

**2. CSRF 방어**

- API 호출은 동일 출처의 페이지에서만 가능
- Referer 헤더 검증

**3. XSS 방어**

- 입력 값 검증 및 이스케이프 처리
- Content-Type 검증

### API 테스트

**Swagger UI 활용**

1. http://localhost:8080/swagger-ui.html 접속
2. 각 API 엔드포인트 선택
3. "Try it out" 버튼 클릭
4. 파라미터 입력 후 "Execute" 실행

**cURL 예시**

```bash
# 회원가입
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# URL 생성
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"original_url":"https://example.com/long/url"}'
```


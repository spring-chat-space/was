# chat-was

Chat Space WAS 레이어입니다.  
비즈니스 로직, DB 처리, Gemini AI 연동을 담당하는 REST API 서버입니다.

## 포트

| 환경 | 포트 |
|------|------|
| local / dev | 8081 |

## 실행

```bash
./gradlew bootRun
```

프로파일 기본값: `local` (`application.yml` → `spring.profiles.active`)

## 주요 기술

| 항목 | 내용 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.0 |
| ORM | Spring Data JPA + Hibernate |
| SQL 매핑 | MyBatis (복잡한 쿼리) |
| DB | PostgreSQL |
| AI | Google Gemini 1.5 Flash |
| 암호화 | AES-256 (JPA AttributeConverter) |
| 빌드 | Gradle 8.13 |

## 디렉터리 구조

```
src/main/
├── java/com/chat/was/
│   ├── auth/
│   │   ├── controller/     # AuthApiController
│   │   ├── service/        # AuthService + impl
│   │   ├── dao/            # AdminUserRepository (JPA)
│   │   └── vo/             # AdminUser (Entity), LoginRequestVo 등
│   ├── chat/
│   │   ├── controller/     # ChatApiController
│   │   ├── service/        # ChatService + impl (Gemini 연동)
│   │   ├── dao/            # ChatRoomRepository, ChatMessageRepository, ChatMapper
│   │   └── vo/             # ChatRoom, ChatMessage (Entity), 요청/응답 Vo
│   └── global/
│       ├── common/         # ApiResponse (공통 응답 포맷)
│       ├── config/         # GeminiConfig, JpaConfig, MyBatisConfig, SecurityConfig
│       ├── crypto/         # AES256Converter
│       └── exception/      # GlobalExceptionHandler
└── resources/
    └── mapper/             # MyBatis XML 매퍼
```

## API 엔드포인트

### 인증

| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |

### 채팅

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/chat/rooms?adminId=` | 채팅방 목록 (최근 수정 순) |
| GET | `/api/v1/chat/rooms/search?adminId=&q=` | 채팅방 검색 |
| POST | `/api/v1/chat/send` | 메시지 전송 + Gemini AI 응답 |
| GET | `/api/v1/chat/rooms/{roomId}/messages?adminId=` | 메시지 히스토리 |
| POST | `/api/v1/chat/rooms/{roomId}/delete?adminId=` | 채팅방 삭제 (논리 삭제) |

## 공통 응답 포맷

```json
{
  "success": true,
  "message": "success",
  "result": { }
}
```

오류 시 `GlobalExceptionHandler`가 동일한 형식으로 래핑하여 반환합니다.

## DB 설정 (로컬)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: 1234
```

PostgreSQL이 실행 중이어야 하며, `ddl-auto: update`로 테이블을 자동 생성합니다.

## 환경 설정 파일

| 파일 | 용도 |
|------|------|
| `application.yml` | 공통 기본값 (JPA, MyBatis, AES 키) |
| `application-local.yml` | 로컬 개발 환경 (DB 접속 정보) |
| `application-dev.yml` | 개발 서버 환경 |
| `application-prod.yml` | 운영 환경 |

## Gemini AI 설정

`application.yml` 또는 환경변수로 API 키를 주입합니다.

```yaml
gemini:
  api-key: ${GEMINI_API_KEY}
  model: gemini-1.5-flash
```

# ERMAS — Eternal Return Multiplayer Analytics System

이터널 리턴(Eternal Return) 전적 조회 서비스입니다.
`premadeCount` 필드를 기준으로 **솔로 / 듀오 / 스쿼드** 전적을 분리하여 통계를 산출합니다.

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3.3, Spring WebFlux (WebClient) |
| Database | PostgreSQL, Redis (추후 도입 예정) |
| Frontend | Vue 3 (Composition API), Vue Router 4, Axios, Tailwind CSS 3, Vite |
| Build | Gradle |
| Test | JUnit 5, AssertJ, MockWebServer (OkHttp) |

---

## API 키 발급 및 환경 설정

프로젝트 루트에 `.env` 파일을 생성하고 API 키를 입력합니다.
`spring-dotenv` 라이브러리가 자동으로 불러오므로 OS 환경 변수 설정 없이도 동작합니다.

```bash
# .env (절대 Git에 커밋하지 않을 것)
ER_API_KEY=발급받은_키
```

`.gitignore`에 `.env`가 등록되어 있습니다.
`.env.example` 파일을 참고해 필요한 변수 목록을 확인하세요.

---

## 프로젝트 구조

```
ERMAS/
├── .env.example                          # 환경 변수 예시 (커밋 포함)
├── frontend/                             # Vue 3 프론트엔드
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js                    # /api 프록시 설정
│   ├── tailwind.config.js
│   └── src/
│       ├── main.js                       # createApp + createRouter + mount
│       ├── App.vue                       # RouterView 래퍼
│       ├── assets/main.css               # Tailwind directives
│       ├── composables/
│       │   └── usePlayerStats.js         # Axios 기반 API 호출 컴포저블
│       ├── views/
│       │   ├── HomeView.vue              # 검색 랜딩 페이지 (/)
│       │   └── PlayerView.vue            # 전적 조회 페이지 (/player/:nickname)
│       └── components/
│           ├── ProfileHeader.vue         # 상단 헤더 + 닉네임 검색
│           ├── QueueStatsSidebar.vue     # 탭 + 승률 원형 차트 + 통계
│           ├── MatchHistoryList.vue      # 매치 기록 리스트
│           └── MatchCard.vue             # 개별 매치 카드
└── src/
    ├── main/
    │   ├── java/com/ermas/ermas/
    │   │   ├── ErmasApplication.java
    │   │   ├── config/
    │   │   │   └── CorsConfig.java               # CORS 허용 설정
    │   │   ├── controller/
    │   │   │   └── GameStatsController.java       # GET /api/stats/{nickname}
    │   │   ├── service/
    │   │   │   ├── ErApiService.java              # WebClient 기반 ER API 호출
    │   │   │   └── StatsAnalyzeService.java       # 큐 타입별 통계 계산
    │   │   └── dto/
    │   │       ├── UserGameDto.java               # 개별 게임 기록
    │   │       ├── UserGamesResponseDto.java      # /v1/user/games 응답 래퍼
    │   │       ├── UserInfoResponseDto.java       # /v1/user 응답 래퍼
    │   │       ├── QueueStatsDto.java             # 큐 타입별 집계 결과
    │   │       └── PlayerStatsResponseDto.java    # 최종 클라이언트 응답
    │   └── resources/
    │       ├── application.yml                    # 서버 설정 + API 환경 변수 참조
    │       └── dummy_games.json                   # 개발용 Mock 데이터 (21게임)
    └── test/
        └── java/com/ermas/ermas/
            ├── GameStatsServiceTest.java          # StatsAnalyzeService 단위 테스트
            └── ErApiServiceTest.java              # MockWebServer 기반 API 테스트
```

---

## 주요 기능

### 큐 타입별 전적 분리

이터널 리턴 API 응답의 `premadeCount` 값을 기준으로 게임을 분류합니다.

| premadeCount | 큐 타입 |
|:---:|------|
| 1 | 솔로 (1인 매칭) |
| 2 | 듀오 (2인 파티) |
| 3 | 스쿼드 (3인 파티) |

### 산출 지표

큐 타입별로 다음 통계를 제공합니다.

- 총 게임 수 및 승리 횟수
- 승률 (`gameRank == 1` 기준)
- 평균 순위
- 평균 킬 (`playerKill`)
- 평균 데미지 (`damageToPlayer`)

### 프론트엔드 UI

- **홈 화면**: 닉네임 검색 입력란 (중앙 정렬)
- **결과 화면**: 상단 헤더(닉네임 + 갱신), 왼쪽 사이드바(탭 + 통계), 오른쪽 매치 히스토리
  - 1위 카드: 파란색 좌측 보더
  - 2~3위 카드: 초록색 좌측 보더

---

## API 명세

### `GET /api/stats/{nickname}`

닉네임으로 유저를 조회하고 큐 타입별 통계와 매치 히스토리를 반환합니다.

**응답 예시 (일부)**

```json
{
  "nickname": "TestPlayer",
  "total": {
    "queueType": "전체",
    "totalGames": 21,
    "totalWins": 8,
    "winRate": 38.1,
    "avgRank": 2.8,
    "avgKills": 5.1,
    "avgDamage": 15523.8
  },
  "solo":  { "queueType": "솔로",   "totalGames": 8, "..." : "..." },
  "duo":   { "queueType": "듀오",   "totalGames": 7, "..." : "..." },
  "squad": { "queueType": "스쿼드", "totalGames": 6, "..." : "..." },
  "games": [ { "gameId": 9000001, "premadeCount": 1, "..." : "..." } ]
}
```

**에러 응답**

| 원인 | HTTP 상태 |
|------|-----------|
| 존재하지 않는 닉네임 | 404 Not Found |
| API 키 오류 | 401 Unauthorized |
| ER API 서버 오류 | 500 Internal Server Error |

---

## 로컬 실행 방법

### 사전 요건

- JDK 17 이상
- Node.js 18 이상
- 이터널 리턴 공식 API 키 ([발급 사이트](https://developer.bser.io))

### 백엔드 실행

```bash
git clone https://github.com/welchs1423/ERMAS.git
cd ERMAS

# 1. 프로젝트 루트에 .env 파일 생성
echo "ER_API_KEY=발급받은_키" > .env

# 2. 서버 실행
./gradlew bootRun
```

API 확인:
```
GET http://localhost:8080/api/stats/{닉네임}
```

### 테스트 실행

```bash
./gradlew test
```

MockWebServer를 사용하므로 실제 API 키 없이도 테스트가 실행됩니다.

### 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 확인: `http://localhost:5173`

> 백엔드와 동시에 실행해야 합니다. Vite의 프록시 설정으로 `/api` 요청이 자동으로 `localhost:8080`으로 전달됩니다.

---

## 진행 현황

- [x] Mock JSON 기반 큐 타입별 통계 API
- [x] 이터널 리턴 공식 API 연동 (WebClient)
- [x] 닉네임 경로 파라미터 지원
- [x] API 키 환경 변수 보안 설정
- [x] Vue 3 + Tailwind CSS 프론트엔드 (홈 화면, 매치 히스토리)
- [ ] PostgreSQL 게임 기록 저장 및 조회
- [ ] Redis 응답 캐싱

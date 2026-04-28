# ERMAS — Eternal Return Multiplayer Analytics System

이터널 리턴(Eternal Return) 전적 검색 서비스입니다.
단순한 시즌 합산 전적이 아닌, `premadeCount` 필드를 기반으로 **솔로큐 / 듀오큐 / 스쿼드큐** 전적을 완전히 분리하여 통계를 제공하는 것이 핵심 차별점입니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3.3, Spring WebFlux (WebClient) |
| Database | PostgreSQL, Redis (향후 연동 예정) |
| Frontend | React 18, Tailwind CSS 3, Vite |
| Build | Gradle |
| Test | JUnit 5, AssertJ, MockWebServer (OkHttp) |

---

## API 연동 및 보안 설정

### 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 API 키를 작성합니다. `spring-dotenv` 라이브러리가 앱 구동 시 자동으로 읽어들이므로 OS 환경 변수를 별도로 주입할 필요가 없습니다.

```bash
# .env (절대 Git에 커밋하지 마세요)
ER_API_KEY=발급받은_키
```

`.gitignore`에 `.env`와 `.env.*`가 등록되어 있으므로 실수로 커밋되지 않습니다.
`.env.example` 파일만 Git에 커밋하여 팀원에게 필요한 변수 목록을 공유하세요.

---

## 프로젝트 구조

```
ERMAS/
├── .env.example                          # 환경 변수 템플릿 (커밋 가능)
├── frontend/                             # React 프론트엔드
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js                    # /api 프록시 설정
│   ├── tailwind.config.js
│   └── src/
│       ├── App.jsx                       # 검색 화면 + 탭 상태 관리
│       ├── hooks/
│       │   └── usePlayerStats.js         # 닉네임 기반 API 호출 훅
│       └── components/
│           ├── ProfileHeader.jsx         # 상단 헤더 + 닉네임 검색
│           ├── QueueStatsSidebar.jsx     # 큐 탭 + 승률 차트 + 통계
│           ├── MatchHistoryList.jsx      # 매치 기록 목록
│           └── MatchCard.jsx            # 단일 매치 카드
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
    │   │   │   └── StatsAnalyzeService.java       # 큐 유형별 통계 계산
    │   │   └── dto/
    │   │       ├── UserGameDto.java               # 단일 게임 기록
    │   │       ├── UserGamesResponseDto.java      # /v1/user/games 응답 래퍼
    │   │       ├── UserInfoResponseDto.java       # /v1/user 응답 래퍼
    │   │       ├── QueueStatsDto.java             # 큐 유형별 집계 결과
    │   │       └── PlayerStatsResponseDto.java    # 최종 클라이언트 응답
    │   └── resources/
    │       ├── application.yml                    # 서버 설정 + API 환경 변수 참조
    │       └── dummy_games.json                   # 테스트용 Mock 데이터 (21게임)
    └── test/
        └── java/com/ermas/ermas/
            ├── GameStatsServiceTest.java          # StatsAnalyzeService 단위 테스트
            └── ErApiServiceTest.java              # MockWebServer 기반 API 테스트
```

---

## 핵심 기능

### 큐 유형별 전적 분리

이터널 리턴 API 응답의 `premadeCount` 값을 기준으로 게임을 분류합니다.

| premadeCount | 큐 유형 |
|:---:|------|
| 1 | 솔로큐 (1인 매칭) |
| 2 | 듀오큐 (2인 파티) |
| 3 | 스쿼드큐 (3인 파티) |

### 계산 지표

각 큐 유형별로 다음 통계를 제공합니다.

- 총 게임 수 및 우승 횟수
- 승률 (`gameRank == 1` 기준)
- 평균 순위
- 평균 킬 수 (`playerKill`)
- 평균 피해량 (`damageToPlayer`)

### 프론트엔드 UI

- **초기 화면**: 닉네임 검색 입력창 (중앙 정렬)
- **결과 화면**: 상단 헤더(닉네임 + 재검색), 좌측 사이드바(큐 탭 + 통계), 우측 매치 리스트
  - 1등 카드: 파란색 좌측 보더
  - 등외 카드: 회색 좌측 보더

---

## API 명세

### `GET /api/stats/{nickname}`

닉네임으로 유저를 조회하고 큐 유형별 통계와 매치 히스토리를 반환합니다.

**응답 예시 (일부)**

```json
{
  "nickname": "TestPlayer",
  "total": {
    "queueType": "전체",
    "totalGames": 50,
    "totalWins": 21,
    "winRate": 42.0,
    "avgRank": 3.1,
    "avgKills": 4.8,
    "avgDamage": 14200.0
  },
  "solo":  { "queueType": "솔로큐",  "totalGames": 20, ... },
  "duo":   { "queueType": "듀오큐",  "totalGames": 18, ... },
  "squad": { "queueType": "스쿼드큐","totalGames": 12, ... },
  "games": [ { "gameId": 9000001, "premadeCount": 1, ... } ]
}
```

**오류 응답**

| 상황 | HTTP 상태 |
|------|-----------|
| 존재하지 않는 닉네임 | 404 Not Found |
| API 키 오류 | 401 Unauthorized |
| ER API 서버 오류 | 500 Internal Server Error |

---

## 로컬 실행 방법

### 사전 요구사항

- JDK 17 이상
- Node.js 18 이상
- 이터널 리턴 공식 API 키 ([발급 페이지](https://developer.bser.io))

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

> 백엔드가 먼저 실행 중이어야 합니다. Vite의 프록시 설정으로 `/api` 요청은 자동으로 `localhost:8080`으로 전달됩니다.

---

## 개발 로드맵

- [x] Mock JSON 기반 큐 유형별 통계 API
- [x] React + Tailwind CSS 프론트엔드 (탭 UI, 매치 히스토리)
- [x] 이터널 리턴 공식 API 연동 (WebClient)
- [x] 닉네임 검색 파라미터 추가
- [x] API 키 환경 변수 보안 설정
- [ ] PostgreSQL 게임 기록 저장 및 조회
- [ ] Redis 응답 캐싱

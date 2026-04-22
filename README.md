# ERMAS — Eternal Return Multiplayer Analytics System

이터널 리턴(Eternal Return) 전적 검색 서비스입니다.
단순한 시즌 합산 전적이 아닌, `premadeCount` 필드를 기반으로 **솔로큐 / 듀오큐 / 스쿼드큐** 전적을 완전히 분리하여 통계를 제공하는 것이 핵심 차별점입니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3.3, Spring Data JPA |
| Database | PostgreSQL, Redis (현재 Mock 단계로 비활성) |
| Frontend | React 18, Tailwind CSS 3, Vite |
| Build | Maven |

---

## 프로젝트 구조

```
ERMAS/
├── frontend/                          # React 프론트엔드
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js                 # /api 프록시 설정
│   ├── tailwind.config.js
│   └── src/
│       ├── App.jsx                    # 탭 상태 관리 및 레이아웃
│       ├── hooks/
│       │   └── usePlayerStats.js      # API 호출 커스텀 훅
│       └── components/
│           ├── ProfileHeader.jsx      # 상단 프로필 헤더
│           ├── QueueStatsSidebar.jsx  # 큐 탭 + 승률 차트 + 통계
│           ├── MatchHistoryList.jsx   # 매치 기록 목록
│           └── MatchCard.jsx         # 단일 매치 카드
└── src/
    ├── main/
    │   ├── java/com/ermas/ermas/
    │   │   ├── ErmasApplication.java
    │   │   ├── config/
    │   │   │   └── CorsConfig.java           # CORS 허용 설정
    │   │   ├── controller/
    │   │   │   └── GameStatsController.java  # REST 엔드포인트
    │   │   ├── service/
    │   │   │   └── GameStatsService.java     # 통계 계산 로직
    │   │   └── dto/
    │   │       ├── UserGameDto.java           # 단일 게임 기록
    │   │       ├── UserGamesResponseDto.java  # API 응답 최상위 래퍼
    │   │       ├── QueueStatsDto.java         # 큐 유형별 집계 결과
    │   │       └── PlayerStatsResponseDto.java # 최종 클라이언트 응답
    │   └── resources/
    │       ├── application.properties
    │       └── dummy_games.json              # Mock 데이터 (21게임)
    └── test/
        └── java/com/ermas/ermas/
            └── GameStatsServiceTest.java
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

닥지지(DAK.GG) 스타일의 이터널 리턴 전적 검색 화면을 구현합니다.

- **상단 프로필 헤더**: 닉네임, 레벨, 전적 갱신 버튼, 시즌 선택
- **좌측 사이드바**: 전체 / 솔로큐 / 듀오큐 / 스쿼드큐 탭 클릭 시 승률 원형 차트와 통계 즉시 갱신
- **우측 매치 리스트**: 탭에 맞는 게임만 필터링하여 카드 형태로 표시
  - 1등 카드: 파란색 좌측 보더
  - 등외 카드: 회색 좌측 보더
  - 큐 타입 뱃지, 매칭 모드(랭크/일반), 킬/도움, 딜량, 아이템 슬롯(6칸)

---

## API 명세

### `GET /api/stats`

전체 및 큐 유형별 통계와 매치 히스토리를 반환합니다.

**응답 예시 (일부)**

```json
{
  "nickname": "TestPlayer",
  "total": {
    "queueType": "전체",
    "totalGames": 21,
    "totalWins": 10,
    "winRate": 47.62,
    "avgRank": 2.57,
    "avgKills": 5.14,
    "avgDamage": 15657.14
  },
  "solo":  { "queueType": "솔로큐",  "totalGames": 8, "totalWins": 3, "winRate": 37.5,  ... },
  "duo":   { "queueType": "듀오큐",  "totalGames": 7, "totalWins": 3, "winRate": 42.86, ... },
  "squad": { "queueType": "스쿼드큐","totalGames": 6, "totalWins": 4, "winRate": 66.67, ... },
  "games": [
    {
      "gameId": 9000001,
      "premadeCount": 1,
      "matchingMode": 3,
      "characterNum": 10,
      "gameRank": 1,
      "playerKill": 7,
      "playerAssistant": 2,
      "damageToPlayer": 18500,
      "startDtm": "2024-03-01T10:00:00",
      "duration": 1350
    }
  ]
}
```

---

## 로컬 실행 방법

### 사전 요구사항

- JDK 17 이상
- Maven 3.8 이상
- Node.js 18 이상

> 현재 Mock 단계이므로 PostgreSQL, Redis 없이 실행 가능합니다.

### 백엔드 실행

```bash
git clone https://github.com/welchs1423/ERMAS.git
cd ERMAS
mvn spring-boot:run
```

서버 기동 후 API 확인:

```
http://localhost:8080/api/stats
```

### 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 확인:

```
http://localhost:5173
```

> 백엔드가 먼저 실행 중이어야 합니다. Vite의 프록시 설정으로 `/api` 요청은 자동으로 `localhost:8080`으로 전달됩니다.

---

## 개발 로드맵

- [x] Mock JSON 기반 큐 유형별 통계 API
- [x] React + Tailwind CSS 프론트엔드 (탭 UI, 매치 히스토리)
- [ ] 이터널 리턴 공식 API 연동 (WebClient)
- [ ] 닉네임 검색 파라미터 추가
- [ ] PostgreSQL 게임 기록 저장 및 조회
- [ ] Redis 응답 캐싱

---

## 실제 API 연동 전환 가이드

`GameStatsService.java`의 `loadUserGames()` 메서드 하나만 교체하면 됩니다.

```java
// Mock: ClassPathResource로 파일 읽기
private List<UserGameDto> loadUserGames() throws IOException {
    ClassPathResource resource = new ClassPathResource(MOCK_DATA_PATH);
    try (InputStream inputStream = resource.getInputStream()) {
        UserGamesResponseDto response = objectMapper.readValue(inputStream, UserGamesResponseDto.class);
        return response.getUserGames();
    }
}

// 실제 API 연동 예시 (WebClient)
private List<UserGameDto> loadUserGames(String nickname) {
    return webClient.get()
            .uri("/v1/user/games/{userNum}", resolveUserNum(nickname))
            .retrieve()
            .bodyToMono(UserGamesResponseDto.class)
            .map(UserGamesResponseDto::getUserGames)
            .block();
}
```

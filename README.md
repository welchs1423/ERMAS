# ERMAS — Eternal Return Multiplayer Analytics System

이터널 리턴(Eternal Return) 전적 검색 서비스입니다.
단순한 시즌 합산 전적이 아닌, `premadeCount` 필드를 기반으로 **솔로큐 / 듀오큐 / 스쿼드큐** 전적을 완전히 분리하여 통계를 제공하는 것이 핵심 차별점입니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3.3, Spring Data JPA |
| Database | PostgreSQL, Redis |
| Frontend | React, Tailwind CSS |
| Build | Maven |

---

## 프로젝트 구조

```
ERMAS/
├── src/
│   ├── main/
│   │   ├── java/com/ermas/ermas/
│   │   │   ├── ErmasApplication.java
│   │   │   ├── controller/
│   │   │   │   └── GameStatsController.java   # REST 엔드포인트
│   │   │   ├── service/
│   │   │   │   └── GameStatsService.java      # 통계 계산 로직
│   │   │   └── dto/
│   │   │       ├── UserGameDto.java            # 단일 게임 기록
│   │   │       ├── UserGamesResponseDto.java   # API 응답 최상위 래퍼
│   │   │       ├── QueueStatsDto.java          # 큐 유형별 집계 결과
│   │   │       └── PlayerStatsResponseDto.java # 최종 클라이언트 응답
│   │   └── resources/
│   │       ├── application.properties
│   │       └── dummy_games.json               # Mock 데이터
│   └── test/
│       └── java/com/ermas/ermas/
│           └── GameStatsServiceTest.java
└── pom.xml
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

- 총 게임 수
- 우승 횟수 및 승률 (`gameRank == 1`)
- 평균 순위
- 평균 킬 수 (`playerKill`)
- 평균 피해량 (`damageToPlayer`)

---

## API 명세

### `GET /api/stats`

현재 Mock 데이터 기반으로 전체 및 큐 유형별 통계를 반환합니다.

**응답 예시**

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
  "solo": {
    "queueType": "솔로큐",
    "totalGames": 8,
    "totalWins": 3,
    "winRate": 37.5,
    "avgRank": 3.13,
    "avgKills": 5.38,
    "avgDamage": 15737.5
  },
  "duo": {
    "queueType": "듀오큐",
    "totalGames": 7,
    "totalWins": 3,
    "winRate": 42.86,
    "avgRank": 2.43,
    "avgKills": 4.14,
    "avgDamage": 14357.14
  },
  "squad": {
    "queueType": "스쿼드큐",
    "totalGames": 6,
    "totalWins": 4,
    "winRate": 66.67,
    "avgRank": 1.5,
    "avgKills": 5.17,
    "avgDamage": 16416.67
  }
}
```

---

## 로컬 실행 방법

### 사전 요구사항

- JDK 17 이상
- Maven 3.8 이상

> 현재 Mock 단계이므로 PostgreSQL, Redis 없이 실행 가능합니다.

### 실행

```bash
git clone https://github.com/welchs1423/ERMAS.git
cd ERMAS
mvn spring-boot:run
```

서버 기동 후 아래 주소로 확인합니다.

```
http://localhost:8080/api/stats
```

---

## 개발 로드맵

- [x] Mock JSON 기반 큐 유형별 통계 API
- [ ] 이터널 리턴 공식 API 연동 (WebClient)
- [ ] PostgreSQL 게임 기록 저장 및 조회
- [ ] Redis 응답 캐싱
- [ ] React 프론트엔드 (전체 / 솔로큐 / 듀오큐 / 스쿼드큐 탭 UI)
- [ ] 닉네임 검색 기능

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

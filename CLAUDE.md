# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ERMAS (Eternal Return Multiplayer Analytics System) is a Spring Boot backend that provides game statistics for the "Eternal Return" multiplayer game, separated by queue type (Solo/Duo/Squad) using the `premadeCount` field. Currently in **Mock/MVP phase** — actual Eternal Return API integration and database persistence are planned but not yet implemented.

## Commands

```bash
# Run development server (port 8080)
mvn spring-boot:run

# Run all tests
mvn test

# Build executable JAR
mvn clean package

# Run single test class
mvn test -Dtest=GameStatsServiceTest
```

## Architecture

**Stack**: Java 17, Spring Boot 3.3.0, Maven, JUnit 5 + AssertJ, Lombok

**Layer structure** (standard Spring MVC):
```
Controller → Service → DTO ← (ClassPathResource: dummy_games.json)
```

- **Controller** (`GameStatsController`): Single endpoint `GET /api/stats` — no query params yet (nickname is hardcoded for mock phase)
- **Service** (`GameStatsService`): Loads `dummy_games.json`, deserializes via Jackson, filters by `premadeCount` (1=솔로, 2=듀오, 3=스쿼드), and computes aggregated stats (winRate, avgRank, avgKills, avgDamage)
- **DTOs**: `UserGameDto` (single game record), `UserGamesResponseDto` (API response wrapper), `QueueStatsDto` (per-queue stats), `PlayerStatsResponseDto` (final response containing all 4 queue objects)

**Response shape**:
```json
{
  "nickname": "TestPlayer",
  "total": { "queueType": "전체", "totalGames": 21, "winRate": ..., ... },
  "solo":  { "queueType": "솔로큐", ... },
  "duo":   { "queueType": "듀오큐", ... },
  "squad": { "queueType": "스쿼드큐", ... }
}
```

## Current State & Roadmap

**Mock phase**: `dummy_games.json` (21 hardcoded games) is the only data source. PostgreSQL and Redis dependencies are already in `pom.xml` but their Spring auto-configurations are **explicitly excluded** in `application.properties` so the app starts without a running DB or Redis.

**Planned next steps** (per README):
1. Replace `dummy_games.json` with Eternal Return official API calls via `WebClient`
2. Persist game records to PostgreSQL via Spring Data JPA
3. Cache responses in Redis
4. Add player nickname as a query parameter
5. React + Tailwind CSS frontend (separate project)

When implementing API integration, the service is the only layer that needs to change — controller and DTOs are already structured for real data.

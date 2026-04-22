package com.ermas.ermas.service;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.dto.QueueStatsDto;
import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.dto.UserGamesResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * dummy_games.json을 파싱하여 큐 유형별 통계를 계산하는 서비스.
 *
 * Mock 단계에서는 ClassPathResource로 JSON 파일을 직접 읽는다.
 * 실제 API 연동 시에는 loadUserGames() 메서드만 WebClient 호출로 교체하면 된다.
 */
@Service
public class GameStatsService {

    private static final String MOCK_DATA_PATH = "dummy_games.json";
    private static final int RANK_WIN = 1;

    private final ObjectMapper objectMapper;

    public GameStatsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 전체 및 큐 유형별(솔로/듀오/스쿼드) 통계를 계산하여 반환한다.
     */
    public PlayerStatsResponseDto getPlayerStats() throws IOException {
        List<UserGameDto> games = loadUserGames();

        // premadeCount 기준으로 그룹 분리
        List<UserGameDto> soloGames  = filterByPremadeCount(games, 1);
        List<UserGameDto> duoGames   = filterByPremadeCount(games, 2);
        List<UserGameDto> squadGames = filterByPremadeCount(games, 3);

        String nickname = games.isEmpty() ? "Unknown" : games.get(0).getNickname();

        return PlayerStatsResponseDto.builder()
                .nickname(nickname)
                .total(calculateStats("전체",    games))
                .solo(calculateStats("솔로큐",  soloGames))
                .duo(calculateStats("듀오큐",   duoGames))
                .squad(calculateStats("스쿼드큐", squadGames))
                .build();
    }

    /**
     * JSON 파일을 읽어 게임 목록을 반환한다.
     * 실제 API 연동 시 이 메서드를 WebClient 호출로 교체한다.
     */
    private List<UserGameDto> loadUserGames() throws IOException {
        ClassPathResource resource = new ClassPathResource(MOCK_DATA_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            UserGamesResponseDto response = objectMapper.readValue(inputStream, UserGamesResponseDto.class);
            return response.getUserGames();
        }
    }

    /**
     * 게임 목록을 premadeCount로 필터링한다.
     */
    private List<UserGameDto> filterByPremadeCount(List<UserGameDto> games, int premadeCount) {
        return games.stream()
                .filter(g -> g.getPremadeCount() == premadeCount)
                .collect(Collectors.toList());
    }

    /**
     * 게임 목록으로부터 승률, 평균 순위, 평균 킬, 평균 딜량을 계산한다.
     * 게임이 없을 경우 모든 수치는 0으로 반환한다.
     */
    private QueueStatsDto calculateStats(String queueType, List<UserGameDto> games) {
        int totalGames = games.size();

        if (totalGames == 0) {
            return QueueStatsDto.builder()
                    .queueType(queueType)
                    .totalGames(0)
                    .totalWins(0)
                    .winRate(0.0)
                    .avgRank(0.0)
                    .avgKills(0.0)
                    .avgDamage(0.0)
                    .build();
        }

        int totalWins    = (int) games.stream().filter(g -> g.getGameRank() == RANK_WIN).count();
        int sumRank      = games.stream().mapToInt(UserGameDto::getGameRank).sum();
        int sumKills     = games.stream().mapToInt(UserGameDto::getPlayerKill).sum();
        long sumDamage   = games.stream().mapToLong(UserGameDto::getDamageToPlayer).sum();

        double winRate   = round((double) totalWins / totalGames * 100);
        double avgRank   = round((double) sumRank   / totalGames);
        double avgKills  = round((double) sumKills  / totalGames);
        double avgDamage = round((double) sumDamage / totalGames);

        return QueueStatsDto.builder()
                .queueType(queueType)
                .totalGames(totalGames)
                .totalWins(totalWins)
                .winRate(winRate)
                .avgRank(avgRank)
                .avgKills(avgKills)
                .avgDamage(avgDamage)
                .build();
    }

    /**
     * 소수점 둘째 자리에서 반올림한다.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

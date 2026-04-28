package com.ermas.ermas.service;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.dto.QueueStatsDto;
import com.ermas.ermas.dto.UserGameDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게임 기록 목록을 받아 큐 유형별 통계를 계산하는 순수 분석 서비스.
 * I/O 없이 계산만 담당하므로 단독으로 단위 테스트가 가능하다.
 */
@Service
public class StatsAnalyzeService {

    private static final int RANK_WIN = 1;

    /**
     * 닉네임과 게임 목록을 받아 전체 및 큐 유형별(솔로/듀오/스쿼드) 통계를 반환한다.
     */
    public PlayerStatsResponseDto analyze(String nickname, List<UserGameDto> games) {
        List<UserGameDto> soloGames  = filterByPremadeCount(games, 1);
        List<UserGameDto> duoGames   = filterByPremadeCount(games, 2);
        List<UserGameDto> squadGames = filterByPremadeCount(games, 3);

        return PlayerStatsResponseDto.builder()
                .nickname(nickname)
                .total(calculateStats("전체",     games))
                .solo(calculateStats("솔로큐",   soloGames))
                .duo(calculateStats("듀오큐",    duoGames))
                .squad(calculateStats("스쿼드큐", squadGames))
                .games(games)
                .build();
    }

    private List<UserGameDto> filterByPremadeCount(List<UserGameDto> games, int premadeCount) {
        return games.stream()
                .filter(g -> g.getPremadeCount() == premadeCount)
                .collect(Collectors.toList());
    }

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

        int totalWins  = (int) games.stream().filter(g -> g.getGameRank() == RANK_WIN).count();
        int sumRank    = games.stream().mapToInt(UserGameDto::getGameRank).sum();
        int sumKills   = games.stream().mapToInt(UserGameDto::getPlayerKill).sum();
        long sumDamage = games.stream().mapToLong(UserGameDto::getDamageToPlayer).sum();

        return QueueStatsDto.builder()
                .queueType(queueType)
                .totalGames(totalGames)
                .totalWins(totalWins)
                .winRate(round((double) totalWins / totalGames * 100))
                .avgRank(round((double) sumRank   / totalGames))
                .avgKills(round((double) sumKills  / totalGames))
                .avgDamage(round((double) sumDamage / totalGames))
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

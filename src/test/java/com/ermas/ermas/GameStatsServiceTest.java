package com.ermas.ermas;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.service.GameStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameStatsServiceTest {

    @Autowired
    private GameStatsService gameStatsService;

    @Test
    void 전체_통계_게임수_확인() throws IOException {
        PlayerStatsResponseDto stats = gameStatsService.getPlayerStats();

        // dummy_games.json 총 21게임 (솔로 8, 듀오 7, 스쿼드 6)
        assertThat(stats.getTotal().getTotalGames()).isEqualTo(21);
        assertThat(stats.getSolo().getTotalGames()).isEqualTo(8);
        assertThat(stats.getDuo().getTotalGames()).isEqualTo(7);
        assertThat(stats.getSquad().getTotalGames()).isEqualTo(6);
    }

    @Test
    void 승률은_0에서_100_사이여야_한다() throws IOException {
        PlayerStatsResponseDto stats = gameStatsService.getPlayerStats();

        assertThat(stats.getTotal().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getSolo().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getDuo().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getSquad().getWinRate()).isBetween(0.0, 100.0);
    }
}

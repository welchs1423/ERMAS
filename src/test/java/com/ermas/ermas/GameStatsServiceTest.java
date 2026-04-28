package com.ermas.ermas;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.dto.UserGamesResponseDto;
import com.ermas.ermas.service.StatsAnalyzeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StatsAnalyzeService 단위 테스트.
 * dummy_games.json을 직접 파싱하여 Spring 컨텍스트 없이 실행한다.
 */
class GameStatsServiceTest {

    private StatsAnalyzeService statsAnalyzeService;
    private List<UserGameDto> dummyGames;

    @BeforeEach
    void setUp() throws IOException {
        statsAnalyzeService = new StatsAnalyzeService();
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("dummy_games.json");
        try (InputStream is = resource.getInputStream()) {
            UserGamesResponseDto response = objectMapper.readValue(is, UserGamesResponseDto.class);
            dummyGames = response.getUserGames();
        }
    }

    @Test
    void 전체_통계_게임수_확인() {
        PlayerStatsResponseDto stats = statsAnalyzeService.analyze("TestPlayer", dummyGames);

        // dummy_games.json 총 21게임 (솔로 8, 듀오 7, 스쿼드 6)
        assertThat(stats.getTotal().getTotalGames()).isEqualTo(21);
        assertThat(stats.getSolo().getTotalGames()).isEqualTo(8);
        assertThat(stats.getDuo().getTotalGames()).isEqualTo(7);
        assertThat(stats.getSquad().getTotalGames()).isEqualTo(6);
    }

    @Test
    void 승률은_0에서_100_사이여야_한다() {
        PlayerStatsResponseDto stats = statsAnalyzeService.analyze("TestPlayer", dummyGames);

        assertThat(stats.getTotal().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getSolo().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getDuo().getWinRate()).isBetween(0.0, 100.0);
        assertThat(stats.getSquad().getWinRate()).isBetween(0.0, 100.0);
    }
}

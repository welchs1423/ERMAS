package com.ermas.ermas.controller;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.service.ErApiService;
import com.ermas.ermas.service.StatsAnalyzeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 게임 통계 조회 REST 컨트롤러.
 * ErApiService로 실제 데이터를 가져온 뒤 StatsAnalyzeService에서 통계를 계산한다.
 */
@RestController
@RequestMapping("/api/stats")
public class GameStatsController {

    private final ErApiService erApiService;
    private final StatsAnalyzeService statsAnalyzeService;

    public GameStatsController(ErApiService erApiService, StatsAnalyzeService statsAnalyzeService) {
        this.erApiService = erApiService;
        this.statsAnalyzeService = statsAnalyzeService;
    }

    /**
     * GET /api/stats/{nickname}
     * 닉네임으로 유저 번호를 조회한 뒤 게임 기록을 가져와 큐 유형별 통계를 반환한다.
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<PlayerStatsResponseDto> getStats(@PathVariable String nickname) {
        long userNum = erApiService.getUserNum(nickname);
        List<UserGameDto> games = erApiService.getUserGames(userNum);
        return ResponseEntity.ok(statsAnalyzeService.analyze(nickname, games));
    }
}

package com.ermas.ermas.controller;

import com.ermas.ermas.dto.PlayerStatsResponseDto;
import com.ermas.ermas.service.GameStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 게임 통계 조회 REST 컨트롤러.
 *
 * Mock 단계에서는 단일 엔드포인트로 dummy_games.json 기반 통계를 반환한다.
 * 실제 API 연동 후에는 @PathVariable로 닉네임을 받아 조회하도록 확장한다.
 */
@RestController
@RequestMapping("/api/stats")
public class GameStatsController {

    private final GameStatsService gameStatsService;

    public GameStatsController(GameStatsService gameStatsService) {
        this.gameStatsService = gameStatsService;
    }

    /**
     * GET /api/stats
     * 전체 및 큐 유형별(솔로/듀오/스쿼드) 통계를 반환한다.
     *
     * 추후 확장 예시:
     * @GetMapping("/{nickname}")
     * public ResponseEntity<PlayerStatsResponseDto> getStats(@PathVariable String nickname) throws IOException
     */
    @GetMapping
    public ResponseEntity<PlayerStatsResponseDto> getStats() throws IOException {
        PlayerStatsResponseDto response = gameStatsService.getPlayerStats();
        return ResponseEntity.ok(response);
    }
}

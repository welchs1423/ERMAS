package com.ermas.ermas.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 특정 큐 유형(솔로/듀오/스쿼드/전체)의 집계 통계를 담는 DTO.
 */
@Getter
@Builder
public class QueueStatsDto {

    // 큐 유형 레이블 (전체, 솔로큐, 듀오큐, 스쿼드큐)
    private String queueType;

    // 총 게임 수
    private int totalGames;

    // 우승 횟수
    private int totalWins;

    // 승률 (0.0 ~ 100.0, 소수점 둘째 자리)
    private double winRate;

    // 평균 순위 (소수점 둘째 자리)
    private double avgRank;

    // 평균 킬 수 (소수점 둘째 자리)
    private double avgKills;

    // 평균 피해량 (소수점 둘째 자리)
    private double avgDamage;
}

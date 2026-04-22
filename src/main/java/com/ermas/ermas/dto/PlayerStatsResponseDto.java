package com.ermas.ermas.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 클라이언트에 반환할 최종 응답 DTO.
 * 전체 및 큐 유형별 통계를 모두 포함한다.
 */
@Getter
@Builder
public class PlayerStatsResponseDto {

    private String nickname;

    // 전체 통계 (모든 게임 합산)
    private QueueStatsDto total;

    // 솔로큐 통계 (premadeCount == 1)
    private QueueStatsDto solo;

    // 듀오큐 통계 (premadeCount == 2)
    private QueueStatsDto duo;

    // 스쿼드큐 통계 (premadeCount == 3)
    private QueueStatsDto squad;
}

package com.ermas.ermas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이터널 리턴 API의 단일 게임 기록을 매핑하는 DTO.
 * 실제 API 응답에 존재하지 않는 필드는 @JsonIgnoreProperties로 무시한다.
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGameDto {

    // 유저 고유 번호
    private long userNum;

    // 닉네임
    private String nickname;

    // 게임 고유 ID
    private long gameId;

    // 시즌 ID
    private int seasonId;

    // 매칭 모드 (2: 일반, 3: 랭크)
    private int matchingMode;

    // 팀 모드 (1: 솔로, 2: 듀오, 3: 스쿼드)
    private int matchingTeamMode;

    // 팀 구성원 수 (1: 솔로, 2: 듀오, 3: 스쿼드)
    private int premadeCount;

    // 사용 캐릭터 번호
    private int characterNum;

    // 최종 순위 (1위 = 우승)
    private int gameRank;

    // 플레이어 처치 수
    private int playerKill;

    // 어시스트 수
    private int playerAssistant;

    // 플레이어에게 가한 피해량
    private int damageToPlayer;

    // 게임 시작 시각
    private String startDtm;

    // 게임 지속 시간 (초)
    private int duration;
}

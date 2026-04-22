package com.ermas.ermas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * dummy_games.json 파일의 최상위 구조를 매핑하는 DTO.
 * 추후 실제 API 응답 파싱 시에도 동일한 구조로 사용한다.
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGamesResponseDto {

    // API 응답 코드
    private int code;

    // 게임 기록 목록
    private List<UserGameDto> userGames;
}

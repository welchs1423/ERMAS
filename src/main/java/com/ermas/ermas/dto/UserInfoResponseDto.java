package com.ermas.ermas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GET /v1/user/{nickname} 응답의 최상위 구조를 매핑하는 DTO.
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponseDto {

    private int code;
    private UserInfo user;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInfo {
        // 유저 고유 번호 — 게임 기록 조회에 사용
        private long userNum;
        private String nickname;
    }
}

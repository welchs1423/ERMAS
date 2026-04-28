package com.ermas.ermas.service;

import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.dto.UserGamesResponseDto;
import com.ermas.ermas.dto.UserInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 이터널 리턴 공식 API를 호출하는 서비스.
 * 헤더에 x-api-key를 담아 요청하며, 응답 코드가 200이 아닌 경우 예외를 던진다.
 */
@Service
public class ErApiService {

    private final WebClient webClient;

    public ErApiService(
            @Value("${er.api.base-url}") String baseUrl,
            @Value("${er.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    /**
     * 닉네임으로 유저 고유 번호(userNum)를 조회한다.
     * API가 code=404를 반환하면 404 예외를 던진다.
     */
    public long getUserNum(String nickname) {
        UserInfoResponseDto response = webClient.get()
                .uri("/v1/user/{nickname}", nickname)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        res -> Mono.error(new ResponseStatusException(res.statusCode(), "ER API 인증 또는 서버 오류"))
                )
                .bodyToMono(UserInfoResponseDto.class)
                .block();

        if (response == null || response.getCode() != 200 || response.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다: " + nickname);
        }
        return response.getUser().getUserNum();
    }

    /**
     * 유저 번호로 최근 게임 기록 목록을 조회한다.
     */
    public List<UserGameDto> getUserGames(long userNum) {
        UserGamesResponseDto response = webClient.get()
                .uri("/v1/user/games/{userNum}", userNum)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        res -> Mono.error(new ResponseStatusException(res.statusCode(), "ER API 인증 또는 서버 오류"))
                )
                .bodyToMono(UserGamesResponseDto.class)
                .block();

        if (response == null || response.getCode() != 200) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "게임 기록 조회 실패");
        }
        return response.getUserGames() != null ? response.getUserGames() : List.of();
    }
}

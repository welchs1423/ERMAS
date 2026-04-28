package com.ermas.ermas.service;

import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.dto.UserGamesResponseDto;
import com.ermas.ermas.dto.UserInfoResponseDto;
import com.ermas.ermas.exception.ErApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class ErApiService {

    private final WebClient webClient;

    public ErApiService(
            @Value("${er.api.base-url}") String baseUrl,
            @Value("${er.api.key}") String apiKey) {

        // API 키 로드 확인 (앞 4자리만 노출)
        String masked = (apiKey != null && apiKey.length() > 4)
                ? apiKey.substring(0, 4) + "****"
                : "****";
        log.info("[ER API] API Key loaded: {}", masked);

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    public long getUserNum(String nickname) {
        UserInfoResponseDto response = webClient.get()
                .uri("/v1/user/{nickname}", nickname)
                .retrieve()
                .onStatus(
                        code -> code.value() == 403,
                        res -> Mono.error(new ErApiException(
                                HttpStatus.UNAUTHORIZED,
                                "API 키가 유효하지 않거나 권한이 없습니다. .env 파일의 ER_API_KEY를 확인하세요."))
                )
                .onStatus(
                        code -> code.value() == 404,
                        res -> Mono.error(new ErApiException(
                                HttpStatus.NOT_FOUND,
                                "유저를 찾을 수 없습니다: " + nickname))
                )
                .onStatus(
                        HttpStatusCode::isError,
                        res -> Mono.error(new ErApiException(
                                HttpStatus.BAD_GATEWAY,
                                "이터널 리턴 API 오류 (status=" + res.statusCode().value() + ")"))
                )
                .bodyToMono(UserInfoResponseDto.class)
                .block();

        if (response == null || response.getCode() != 200 || response.getUser() == null) {
            throw new ErApiException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다: " + nickname);
        }
        return response.getUser().getUserNum();
    }

    public List<UserGameDto> getUserGames(long userNum) {
        UserGamesResponseDto response = webClient.get()
                .uri("/v1/user/games/{userNum}", userNum)
                .retrieve()
                .onStatus(
                        code -> code.value() == 403,
                        res -> Mono.error(new ErApiException(
                                HttpStatus.UNAUTHORIZED,
                                "API 키가 유효하지 않거나 권한이 없습니다. .env 파일의 ER_API_KEY를 확인하세요."))
                )
                .onStatus(
                        HttpStatusCode::isError,
                        res -> Mono.error(new ErApiException(
                                HttpStatus.BAD_GATEWAY,
                                "이터널 리턴 API 오류 (status=" + res.statusCode().value() + ")"))
                )
                .bodyToMono(UserGamesResponseDto.class)
                .block();

        if (response == null || response.getCode() != 200) {
            throw new ErApiException(HttpStatus.BAD_GATEWAY, "게임 기록 조회 실패");
        }
        return response.getUserGames() != null ? response.getUserGames() : List.of();
    }
}

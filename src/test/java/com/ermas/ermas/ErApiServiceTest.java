package com.ermas.ermas;

import com.ermas.ermas.dto.UserGameDto;
import com.ermas.ermas.service.ErApiService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ErApiService 단위 테스트.
 * MockWebServer로 실제 외부 API 없이 HTTP 요청/응답을 검증한다.
 */
class ErApiServiceTest {

    private MockWebServer mockWebServer;
    private ErApiService erApiService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        // MockWebServer의 URL을 base-url로 주입하여 외부 네트워크 없이 테스트
        erApiService = new ErApiService(
                mockWebServer.url("/").toString(),
                "test-api-key"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getUserNum_정상응답_유저번호반환() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\":200,\"user\":{\"userNum\":100001,\"nickname\":\"TestPlayer\"}}"));

        long userNum = erApiService.getUserNum("TestPlayer");

        assertThat(userNum).isEqualTo(100001L);
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/v1/user/TestPlayer");
        assertThat(request.getHeader("x-api-key")).isEqualTo("test-api-key");
    }

    @Test
    void getUserGames_정상응답_게임목록반환() throws InterruptedException {
        String body = "{\"code\":200,\"userGames\":["
                + "{\"gameId\":9000001,\"premadeCount\":1,\"gameRank\":1,"
                + "\"playerKill\":7,\"playerAssistant\":2,\"damageToPlayer\":18500}"
                + "]}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body));

        List<UserGameDto> games = erApiService.getUserGames(100001L);

        assertThat(games).hasSize(1);
        assertThat(games.get(0).getGameId()).isEqualTo(9000001L);
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/v1/user/games/100001");
    }

    @Test
    void getUserNum_코드404_예외발생() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\":404,\"message\":\"유저를 찾을 수 없습니다.\"}"));

        assertThatThrownBy(() -> erApiService.getUserNum("없는유저"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getUserNum_HTTP401_예외발생() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        assertThatThrownBy(() -> erApiService.getUserNum("TestPlayer"))
                .isInstanceOf(ResponseStatusException.class);
    }
}

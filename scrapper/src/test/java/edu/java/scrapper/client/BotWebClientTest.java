package edu.java.scrapper.client;

import edu.java.client.impl.BotWebClient;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;

public class BotWebClientTest extends HttpClientTest {

    private static BotWebClient botWebClient;

    private static LinkUpdateRequest linkUpdateRequest;

    private static final String UPDATES_PATH = "/updates";

    private static final String SUCCESS_UPDATE_MESSAGE = "Обновление обработано";

    @BeforeAll
    public static void setUp() {
        botWebClient = new BotWebClient(baseUrl);
        List<HttpStatus> retryStatusCodes = List.of(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY);
        botWebClient.setRetryStatusCodes(retryStatusCodes);

        linkUpdateRequest = LinkUpdateRequest.builder()
            .id(123L).url("https://github.com/wiremock/wiremock")
            .updatesDescription("Появился новый коммит от пользователя tomakehurst")
            .telegramChatIds(List.of(89487L, 1989798L))
            .build();
    }

    @Test
    public void testSuccessSendUpdate() {
        String json = "{ \"message\": \"%s\" }".formatted(SUCCESS_UPDATE_MESSAGE);

        wireMockServer
            .stubFor(post(UPDATES_PATH)
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        LinkUpdateResponse response = botWebClient.sendUpdate(linkUpdateRequest);
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(SUCCESS_UPDATE_MESSAGE);
    }
}

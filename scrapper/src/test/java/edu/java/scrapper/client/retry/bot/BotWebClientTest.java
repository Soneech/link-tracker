package edu.java.scrapper.client.retry.bot;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.client.BotClient;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BotWebClientTest extends BotClientTest {

    @Autowired
    private BotClient botWebClient;

    private static LinkUpdateRequest linkUpdateRequest;

    private static final String SUCCESS_UPDATE_MESSAGE = "Обновление обработано";

    private static final String SCENARIO_NAME = "retry-scenario";

    private static final String BOT_UNAVAILABLE_MESSAGE = "Bot unavailable";

    @BeforeAll
    public static void setUp() {
        linkUpdateRequest = LinkUpdateRequest.builder()
            .id(123L).url("https://github.com/wiremock/wiremock")
            .updatesDescription("Появился новый коммит от пользователя tomakehurst")
            .telegramChatIds(List.of(89487L, 1989798L))
            .build();
    }

    @Test
    public void testSuccessRetrySendUpdate() {
        String jsonBody = "{ \"message\": \"%s\" }".formatted(SUCCESS_UPDATE_MESSAGE);

        stubFailedSendUpdateState(SCENARIO_NAME, Scenario.STARTED, "state2", 503);
        stubSuccessSendUpdateState(SCENARIO_NAME, "state2", 200, jsonBody);

        LinkUpdateResponse response = botWebClient.sendUpdate(linkUpdateRequest);
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(SUCCESS_UPDATE_MESSAGE);
    }

    @Test
    public void testFailedRetrySendUpdate() {
        stubFailedSendUpdateState(SCENARIO_NAME, Scenario.STARTED, "state2", 500);
        stubFailedSendUpdateState(SCENARIO_NAME, "state2", "state3", 500);
        stubFailedSendUpdateState(SCENARIO_NAME, "state3", "state4", 500);

        LinkUpdateResponse expectedResponse = new LinkUpdateResponse(BOT_UNAVAILABLE_MESSAGE);
        LinkUpdateResponse actualResponse = botWebClient.sendUpdate(linkUpdateRequest);
        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }
}

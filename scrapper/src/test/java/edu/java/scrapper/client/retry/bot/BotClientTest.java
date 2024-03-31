package edu.java.scrapper.client.retry.bot;

import edu.java.scrapper.client.retry.HttpClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class BotClientTest extends HttpClientTest {

    private static final String UPDATES_PATH = "/updates";

    public void stubFailedSendUpdateState(String scenarioName, String currentState, String newState, int statusCode) {
        wireMockServer
            .stubFor(post(UPDATES_PATH)
                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessSendUpdateState(String scenarioName, String currentState, int statusCode, String jsonBody) {
        wireMockServer
            .stubFor(post(UPDATES_PATH)
                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }
}

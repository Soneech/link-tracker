package edu.java.bot.client.retry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;

public class ScrapperClientTest extends HttpClientTest {

    private static final String CHAT_ID_HEADER = "Tg-Chat-Id";

    private static final String LINK_ENDPOINTS_PATH = "/links";

    private static final String CHAT_ENDPOINTS_PATH = "/tg-chat/{chatId}";

    private static final String CHAT_ID_PARAM = "chatId";

    public void stubFailedRegistrationState(String scenarioName, String currentState,
                                            String newState, int statusCode, long chatId) {
        wireMockServer
            .stubFor(post(urlPathTemplate(CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessRegistrationState(String scenarioName, String currentState,
                                             int statusCode, String jsonResponseBody, long chatId) {
        wireMockServer
            .stubFor(post(urlPathTemplate(CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubFailedDeleteChatState(String scenarioName, String currentState,
        String newState, int statusCode, long chatId) {
        wireMockServer
            .stubFor(delete(urlPathTemplate(CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessDeleteChatState(String scenarioName, String currentState,
        int statusCode, String jsonResponseBody, long chatId) {
        wireMockServer
            .stubFor(delete(urlPathTemplate(CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubFailedGetLinksState(String scenarioName, String currentState,
        String newState, int statusCode, long chatId) {
        wireMockServer
            .stubFor(get(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessGetLinksState(String scenarioName, String currentState,
        int statusCode, String jsonResponseBody, long chatId) {
        wireMockServer
            .stubFor(get(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubFailedAddLinkState(String scenarioName, String currentState,
        String newState, int statusCode, long chatId) {
        wireMockServer
            .stubFor(post(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessAddLinkState(String scenarioName, String currentState,
        int statusCode, String jsonResponseBody, long chatId) {
        wireMockServer
            .stubFor(post(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubFailedDeleteLinkState(String scenarioName, String currentState,
        String newState, int statusCode, long chatId) {
        wireMockServer
            .stubFor(delete(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessDeleteLinkState(String scenarioName, String currentState,
        int statusCode, String jsonResponseBody, long chatId) {
        wireMockServer
            .stubFor(delete(LINK_ENDPOINTS_PATH)
                .withHeader(CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }
}

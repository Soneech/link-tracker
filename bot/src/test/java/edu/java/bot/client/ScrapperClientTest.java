package edu.java.bot.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;

public abstract class ScrapperClientTest extends HttpClientTest {

    private static final String TELEGRAM_CHAT_ID_HEADER = "Tg-Chat-Id";

    private static final String LINK_ENDPOINTS_PATH = "/links";

    private static final String TELEGRAM_CHAT_ENDPOINTS_PATH = "/tg-chat/{chatId}";

    private static final String CHAT_ID_PARAM = "chatId";

    public void stubForRegisterChat(int statusCode, String jsonBody, long chatId) {
        wireMockServer
            .stubFor(post(urlPathTemplate(TELEGRAM_CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubForDeleteChat(int statusCode, String jsonBody, long chatId) {
        wireMockServer
            .stubFor(delete(urlPathTemplate(TELEGRAM_CHAT_ENDPOINTS_PATH))
                .withPathParam(CHAT_ID_PARAM, equalTo(String.valueOf(chatId)))
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubForGetLinks(int statusCode, String jsonBody, long chatId) {
        wireMockServer
            .stubFor(get(LINK_ENDPOINTS_PATH)
                .withHeader(TELEGRAM_CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubForAddLink(int statusCode, String jsonBody, long chatId) {
        wireMockServer
            .stubFor(post(LINK_ENDPOINTS_PATH)
                .withHeader(TELEGRAM_CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubForDeleteLink(int statusCode, String jsonBody, long chatId) {
        wireMockServer
            .stubFor(delete(LINK_ENDPOINTS_PATH)
                .withHeader(TELEGRAM_CHAT_ID_HEADER, equalTo(String.valueOf(chatId)))
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }
}

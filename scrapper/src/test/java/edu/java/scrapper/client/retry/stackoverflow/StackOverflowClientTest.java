package edu.java.scrapper.client.retry.stackoverflow;

import edu.java.scrapper.client.retry.HttpClientTest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;

public class StackOverflowClientTest extends HttpClientTest {

    private static final String QUESTION_PATH = "/questions/{questionId}";

    private static final String ANSWERS_PATH = "/questions/{questionId}/answers";

    private static final String QUESTION_ID_PARAM = "questionId";

    private static final Pair<String, String> SITE_PARAM = Pair.of("site", "stackoverflow");

    private static final Pair<String, String> SORT_PARAM = Pair.of("sort", "creation");

    public void stubFailedFetchQuestionState(String scenarioName, String currentState, String newState,
        int statusCode, long questionId) {

        wireMockServer
            .stubFor(get(urlPathTemplate(QUESTION_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(questionId)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)
                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessFetchQuestionState(String scenarioName, String currentState, int statusCode,
        String jsonResponseBody, long questionId) {

        wireMockServer
            .stubFor(get(urlPathTemplate(QUESTION_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(questionId)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }

    public void stubFailedFetchQuestionAnswersState(String scenarioName, String currentState, String newState,
        int statusCode, long questionId) {

        wireMockServer
            .stubFor(get(urlPathTemplate(ANSWERS_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(questionId)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))
                .withQueryParam(SORT_PARAM.getKey(), equalTo(SORT_PARAM.getValue()))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessFetchQuestionAnswersState(String scenarioName, String currentState, int statusCode,
        String jsonResponseBody, long questionId) {

        wireMockServer
            .stubFor(get(urlPathTemplate(ANSWERS_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(questionId)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))
                .withQueryParam(SORT_PARAM.getKey(), equalTo(SORT_PARAM.getValue()))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }
}

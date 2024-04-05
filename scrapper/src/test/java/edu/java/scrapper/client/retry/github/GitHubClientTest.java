package edu.java.scrapper.client.retry.github;

import edu.java.scrapper.client.retry.HttpClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;

public class GitHubClientTest extends HttpClientTest {

    private static final String REPOSITORY_PATH = "/repos/{username}/{repository}";

    private static final String EVENTS_PATH = "/repos/{username}/{repository}/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String USERNAME_PARAM = "username";

    private static final String REPOSITORY_PARAM = "repository";

    public void stubFailedCheckThatRepositoryExistsState(String scenarioName, String currentState,
        String newState, int statusCode, String userName, String repository) {

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(userName))
                .withPathParam(REPOSITORY_PARAM, equalTo(repository))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessCheckThatRepositoryExistsState(String scenarioName, String currentState,
        int statusCode, String jsonResponseBody, String userName, String repository) {

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(userName))
                .withPathParam(REPOSITORY_PARAM, equalTo(repository))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

    }

    public void stubFailedFetchRepositoryEventsState(String scenarioName, String currentState,
        String newState, int statusCode, int eventsCount, String userName, String repository) {

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(userName))
                .withPathParam(REPOSITORY_PARAM, equalTo(repository))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(newState)

                .willReturn(aResponse()
                    .withStatus(statusCode)));
    }

    public void stubSuccessFetchRepositoryEventsState(String scenarioName, String currentState,
        int statusCode, int eventsCount, String jsonResponseBody, String userName, String repository) {

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(userName))
                .withPathParam(REPOSITORY_PARAM, equalTo(repository))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))

                .inScenario(scenarioName)
                .whenScenarioStateIs(currentState)

                .willReturn(aResponse()
                    .withStatus(statusCode)
                    .withBody(jsonResponseBody)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }
}

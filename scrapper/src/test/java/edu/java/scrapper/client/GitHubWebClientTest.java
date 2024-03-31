package edu.java.scrapper.client;

import edu.java.client.impl.GitHubWebClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class GitHubWebClientTest extends HttpClientTest {
    private static GitHubWebClient gitHubWebClient;

    private static final String REPOSITORY_PATH = "/repos/{username}/{repository}";

    private static final String EVENTS_PATH = "/repos/{username}/{repository}/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String USERNAME_PARAM = "username";

    private static final String REPOSITORY_PARAM = "repository";

    private static final String FIRST_USER_NAME = "Soneech";

    private static final String FIRST_REPOSITORY = "link-tracker";

    private static final String SECOND_USER_NAME = "sanyarnd";

    private static final String SECOND_REPOSITORY = "java-course-2023-backend-template";

    private static final String INVALID_USER_NAME = "invalid-user-name";

    private static final String INVALID_REPOSITORY = "invalid-repo-name";

    @BeforeAll
    public static void clientSetUp() {
        gitHubWebClient = new GitHubWebClient(baseUrl, "some-token", eventsCount);
        List<HttpStatus> retryStatusCodes = List.of(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY);
        gitHubWebClient.setRetryStatusCodes(retryStatusCodes);
    }

    @Test
    public void testSuccessCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:repo-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(FIRST_USER_NAME))
                .withPathParam(REPOSITORY_PARAM, equalTo(FIRST_REPOSITORY))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        RepositoryInfoResponse response = gitHubWebClient.checkThatRepositoryExists(FIRST_USER_NAME, FIRST_REPOSITORY);

        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo(FIRST_USER_NAME + "/" + FIRST_REPOSITORY);
    }

    @Test
    public void testFailedCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(INVALID_USER_NAME))
                .withPathParam(REPOSITORY_PARAM, equalTo(INVALID_REPOSITORY))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> gitHubWebClient.checkThatRepositoryExists(INVALID_USER_NAME, INVALID_REPOSITORY));
    }

    @Test
    public void testSuccessFetchPushEvent() throws IOException {
        File file = ResourceUtils.getFile("classpath:events-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(SECOND_USER_NAME))
                .withPathParam(REPOSITORY_PARAM, equalTo(SECOND_REPOSITORY))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> events = gitHubWebClient.fetchRepositoryEvents(SECOND_USER_NAME, SECOND_REPOSITORY);
        assertThat(events).isNotEmpty().hasSize(1);
        assertThat(events.getFirst().actor().login()).isEqualTo(SECOND_USER_NAME);
    }

    @Test
    public void testFailedFetchPushEvent() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(INVALID_USER_NAME))
                .withPathParam(REPOSITORY_PARAM, equalTo(INVALID_REPOSITORY))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> gitHubWebClient.fetchRepositoryEvents(INVALID_USER_NAME, INVALID_REPOSITORY));
    }
}

package edu.java.scrapper.client;

import edu.java.client.impl.GitHubWebClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
    private GitHubWebClient gitHubWebClient;

    private static final String REPOSITORY_PATH = "/repos/{username}/{repository}";

    private static final String EVENTS_PATH = "/repos/{username}/{repository}/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String USERNAME_PARAM = "username";

    private static final String REPOSITORY_PARAM = "repository";

    @BeforeEach
    public void setUp() {
        gitHubWebClient = new GitHubWebClient(baseUrl, "some-token", eventsCount);
        List<HttpStatus> errorStatusCodes = List.of(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY);
        gitHubWebClient.setErrorStatusCodes(errorStatusCodes);
    }

    @Test
    public void testSuccessCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:repo-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String username = "Soneech";
        String repository = "link-tracker";
        long repositoryId = 751786052;

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(username))
                .withPathParam(REPOSITORY_PARAM, equalTo(repository))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        RepositoryInfoResponse response = gitHubWebClient.checkThatRepositoryExists(username, repository);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(repositoryId);
        assertThat(response.fullName()).isEqualTo(username + "/" + repository);
    }

    @Test
    public void testFailedCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String invalidUserName = "invalid-user-name";
        String invalidRepo = "invalid-repo-name";

        wireMockServer
            .stubFor(get(urlPathTemplate(REPOSITORY_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(invalidUserName))
                .withPathParam(REPOSITORY_PARAM, equalTo(invalidRepo))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> gitHubWebClient.checkThatRepositoryExists(invalidUserName, invalidRepo));
    }

    @Test
    public void testSuccessFetchPushEvent() throws IOException {
        File file = ResourceUtils.getFile("classpath:events-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String userName = "sanyarnd";
        String repositoryName = "java-course-2023-backend-template";

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(userName))
                .withPathParam(REPOSITORY_PARAM, equalTo(repositoryName))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> events = gitHubWebClient.fetchRepositoryEvents(userName, repositoryName);
        assertThat(events).isNotEmpty().hasSize(1);
        assertThat(events.getFirst().actor().login()).isEqualTo(userName);
    }

    @Test
    public void testFailedFetchPushEvent() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String invalidUserName = "invalid-user-name";
        String invalidRepo = "invalid-repo-name";

        wireMockServer
            .stubFor(get(urlPathTemplate(EVENTS_PATH))
                .withPathParam(USERNAME_PARAM, equalTo(invalidUserName))
                .withPathParam(REPOSITORY_PARAM, equalTo(invalidRepo))
                .withQueryParam(PER_PAGE_PARAM, equalTo(String.valueOf(eventsCount)))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> gitHubWebClient.fetchRepositoryEvents(invalidUserName, invalidRepo));
    }
}

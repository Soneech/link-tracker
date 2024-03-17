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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class GitHubWebClientTest extends HttpClientTest {
    private GitHubWebClient gitHubWebClient;

    private final String repositoryName = "link-tracker";

    private final String userName = "Soneech";

    private final String repositoryPath = "/repos/%s/%s";

    private final String eventsPath = "/repos/%s/%s/events?per_page=%d";

    @Value("${api.github.events-count}")
    private int eventsCount;

    @BeforeEach
    public void setUp() {
        gitHubWebClient = new GitHubWebClient(baseUrl);
        gitHubWebClient.setEventsCount(eventsCount);
    }

    @Test
    public void testSuccessCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:repo-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(repositoryPath.formatted(userName, repositoryName))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        RepositoryInfoResponse response = gitHubWebClient.checkThatRepositoryExists(userName, repositoryName);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(751786052L);
        assertThat(response.fullName()).isEqualTo(userName + "/" + repositoryName);
    }

    @Test
    public void testFailedCheckRepositoryExistence() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String invalidUserName = "invalid-user-name";
        String invalidRepo = "invalid-repo-name";

        wireMockServer
            .stubFor(get(repositoryPath.formatted(invalidUserName, invalidRepo))
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
            .stubFor(get(eventsPath.formatted(userName, repositoryName, eventsCount))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> events = gitHubWebClient.fetchRepositoryEvents(userName, repositoryName);
        assertThat(events).isNotEmpty();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().actor().login()).isEqualTo("sanyarnd");
    }

    @Test
    public void testFailedFetchPushEvent() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String invalidUserName = "invalid-user-name";
        String invalidRepo = "invalid-repo-name";

        wireMockServer
            .stubFor(get(eventsPath.formatted(invalidUserName, invalidRepo, eventsCount))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> gitHubWebClient.fetchRepositoryEvents(invalidUserName, invalidRepo));
    }
}

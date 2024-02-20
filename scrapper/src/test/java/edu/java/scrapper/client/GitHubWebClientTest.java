package edu.java.scrapper.client;

import edu.java.client.impl.GitHubWebClient;
import edu.java.dto.github.RepositoryResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GitHubWebClientTest extends HttpClientTest {
    private GitHubWebClient gitHubWebClient;

    private final String repositoryName = "link-tracker";

    private final String userName = "Soneech";

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        gitHubWebClient = new GitHubWebClient(baseUrl);
    }

    @Test
    public void testGettingRepositoryInfo() throws IOException {
        File file = ResourceUtils.getFile("classpath:repo-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s".formatted(userName, repositoryName))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        RepositoryResponse response = gitHubWebClient.fetchRepository(userName, repositoryName);

        assertNotNull(response);
        assertThat(response.id()).isEqualTo(751786052L);
        assertThat(response.owner().name()).isEqualTo(userName);
        assertThat(response.repositoryName()).isEqualTo(repositoryName);
    }

    @Test
    public void testResponseOnInvalidRepository() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-repo.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String invalidUserName = "invalid user name";
        String invalidRepo = "invalid repo name";

        wireMockServer
            .stubFor(get("/repos/%s/%s".formatted(invalidUserName, invalidRepo))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(WebClientResponseException.class)
            .isThrownBy(() -> gitHubWebClient.fetchRepository(invalidUserName, invalidRepo));
    }
}

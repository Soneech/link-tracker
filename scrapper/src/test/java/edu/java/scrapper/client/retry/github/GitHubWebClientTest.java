package edu.java.scrapper.client.retry.github;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.ResourceUnavailableException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class GitHubWebClientTest extends GitHubClientTest {

    @Autowired
    private GitHubClient gitHubWebClient;

    @Value("${api.github.events-count}")
    private int eventsCount;

    private static final String FIRST_USER_NAME = "Soneech";

    private static final String FIRST_REPOSITORY = "link-tracker";

    private static final String SECOND_USER_NAME = "sanyarnd";

    private static final String SECOND_REPOSITORY = "java-course-2023-backend-template";

    private static final String SCENARIO_NAME = "stackoverflow-scenario";

    @Test
    public void testSuccessRetryCheckThatRepositoryExists() throws IOException {
        File file = ResourceUtils.getFile("classpath:repo-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        stubFailedCheckThatRepositoryExistsState(SCENARIO_NAME, Scenario.STARTED, "state2",
            504, FIRST_USER_NAME, FIRST_REPOSITORY);
        stubFailedCheckThatRepositoryExistsState(SCENARIO_NAME, "state2", "state3",
            500, FIRST_USER_NAME, FIRST_REPOSITORY);
        stubSuccessCheckThatRepositoryExistsState(SCENARIO_NAME, "state3",
            200, json, FIRST_USER_NAME, FIRST_REPOSITORY);

        RepositoryInfoResponse response = gitHubWebClient.checkThatRepositoryExists(FIRST_USER_NAME, FIRST_REPOSITORY);
        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo(FIRST_USER_NAME + "/" + FIRST_REPOSITORY);
    }

    @Test
    public void testFailedRetryCheckThatRepositoryExists() {

        stubFailedCheckThatRepositoryExistsState(SCENARIO_NAME, Scenario.STARTED, "state2",
            500, FIRST_USER_NAME, FIRST_REPOSITORY);
        stubFailedCheckThatRepositoryExistsState(SCENARIO_NAME, "state2", "state3",
            500, FIRST_USER_NAME, FIRST_REPOSITORY);
        stubFailedCheckThatRepositoryExistsState(SCENARIO_NAME, "state3", "state4",
            502, FIRST_USER_NAME, FIRST_REPOSITORY);

        ResourceUnavailableException exception = assertThrows(ResourceUnavailableException.class,
            () -> gitHubWebClient.checkThatRepositoryExists(FIRST_USER_NAME, FIRST_REPOSITORY));
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(502);
    }

    @Test
    public void testSuccessRetryFetchRepositoryEvents() throws IOException {
        File file = ResourceUtils.getFile("classpath:events-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        stubFailedFetchRepositoryEventsState(SCENARIO_NAME, Scenario.STARTED, "state2",
            502, eventsCount, SECOND_USER_NAME, SECOND_REPOSITORY);
        stubSuccessFetchRepositoryEventsState(SCENARIO_NAME, "state2",
            200, eventsCount, json, SECOND_USER_NAME, SECOND_REPOSITORY);

        List<EventResponse> events = gitHubWebClient.fetchRepositoryEvents(SECOND_USER_NAME, SECOND_REPOSITORY);
        assertThat(events).isNotEmpty().hasSize(1);
        assertThat(events.getFirst().actor().login()).isEqualTo(SECOND_USER_NAME);
    }

    @Test
    public void testFailedRetryFetchRepositoryEvents() {
        stubFailedFetchRepositoryEventsState(SCENARIO_NAME, Scenario.STARTED, "state2",
            500, eventsCount, SECOND_USER_NAME, SECOND_REPOSITORY);
        stubFailedFetchRepositoryEventsState(SCENARIO_NAME, "state2", "state3",
            500, eventsCount, SECOND_USER_NAME, SECOND_REPOSITORY);
        stubFailedFetchRepositoryEventsState(SCENARIO_NAME, "state3", "state4",
            504, eventsCount, SECOND_USER_NAME, SECOND_REPOSITORY);

        List<EventResponse> response = gitHubWebClient.fetchRepositoryEvents(SECOND_USER_NAME, SECOND_REPOSITORY);
        assertThat(response).isEmpty();
    }
}

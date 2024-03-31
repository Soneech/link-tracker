package edu.java.scrapper.client.retry.stackoverflow;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.AnswersResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.exception.ResourceUnavailableException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class StackOverflowWebClientTest extends StackOverflowClientTest {

    @Autowired
    private StackOverflowClient stackOverflowWebClient;

    private static final Long QUESTION_ID = 28892948L;

    private static final String SCENARIO_NAME = "stackoverflow-scenario";

    @Test
    public void testSuccessRetryFetchQuestion() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        stubFailedFetchQuestionState(SCENARIO_NAME, Scenario.STARTED, "state2", 504, QUESTION_ID);
        stubSuccessFetchQuestionState(SCENARIO_NAME, "state2", 200, json, QUESTION_ID);

        QuestionResponse response = stackOverflowWebClient.fetchQuestion(QUESTION_ID);
        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty().hasSize(1);
    }

    @Test
    public void testFailedRetryFetchQuestion() {
        stubFailedFetchQuestionState(SCENARIO_NAME, Scenario.STARTED, "state2", 500, QUESTION_ID);
        stubFailedFetchQuestionState(SCENARIO_NAME, "state2", "state3", 504, QUESTION_ID);
        stubFailedFetchQuestionState(SCENARIO_NAME, "state3", "state4", 504, QUESTION_ID);

        ResourceUnavailableException exception = assertThrows(ResourceUnavailableException.class, () -> {
            stackOverflowWebClient.fetchQuestion(QUESTION_ID);
        });
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(504);
    }

    @Test
    public void testSuccessRetryFetchQuestionAnswers() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-answers-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        stubFailedFetchQuestionAnswersState(SCENARIO_NAME, Scenario.STARTED, "state2",
            500, QUESTION_ID);
        stubSuccessFetchQuestionAnswersState(SCENARIO_NAME, Scenario.STARTED, 200, json, QUESTION_ID);

        AnswersResponse response = stackOverflowWebClient.fetchQuestionAnswers(QUESTION_ID);
        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty().hasSize(2);
    }

    @Test
    public void testFailedRetryFetchQuestionAnswers() {
        stubFailedFetchQuestionAnswersState(SCENARIO_NAME, Scenario.STARTED, "state2",
            500, QUESTION_ID);
        stubFailedFetchQuestionAnswersState(SCENARIO_NAME, "state2", "state3",
            500, QUESTION_ID);
        stubFailedFetchQuestionAnswersState(SCENARIO_NAME, "state3", "state4",
            504, QUESTION_ID);

        AnswersResponse response = stackOverflowWebClient.fetchQuestionAnswers(QUESTION_ID);
        assertThat(response).isNotNull();
        assertThat(response.items()).isEmpty();
    }
}

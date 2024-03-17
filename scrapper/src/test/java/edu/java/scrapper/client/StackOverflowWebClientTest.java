package edu.java.scrapper.client;

import edu.java.client.impl.StackOverflowWebClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowWebClientTest extends HttpClientTest {
    private final Long questionId = 28892948L;

    private StackOverflowWebClient stackOverflowWebClient;

    private final String questionPath = "/questions/%d?site=stackoverflow";

    private final String answersPath = "/questions/%d/answers?site=stackoverflow&sort=creation";

    @BeforeEach
    public void setUp() {
        stackOverflowWebClient = new StackOverflowWebClient(baseUrl);
    }

    @Test
    public void testSuccessFetchQuestion() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(questionPath.formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty();
        assertThat(response.items()).hasSize(1);
    }

    @Test
    public void testFetchQuestionAnswers() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-answers-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(answersPath.formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowWebClient.fetchQuestionAnswers(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty();
        assertThat(response.items()).hasSize(2);
    }
}

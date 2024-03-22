package edu.java.scrapper.client;

import edu.java.client.impl.StackOverflowWebClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowWebClientTest extends HttpClientTest {

    private StackOverflowWebClient stackOverflowWebClient;

    private static final Long QUESTION_ID = 28892948L;

    private static final String QUESTION_PATH = "/questions/{questionId}";

    private static final String ANSWERS_PATH = "/questions/{questionId}/answers";

    private static final String QUESTION_ID_PARAM = "questionId";

    private static final Pair<String, String> SITE_PARAM = Pair.of("site", "stackoverflow");

    private static final Pair<String, String> SORT_PARAM = Pair.of("sort", "creation");

    @BeforeEach
    public void setUp() {
        stackOverflowWebClient = new StackOverflowWebClient(baseUrl);
    }

    @Test
    public void testSuccessFetchQuestion() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(QUESTION_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(QUESTION_ID)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowWebClient.fetchQuestion(QUESTION_ID);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty().hasSize(1);
    }

    @Test
    public void testFetchQuestionAnswers() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-answers-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get(urlPathTemplate(ANSWERS_PATH))
                .withPathParam(QUESTION_ID_PARAM, equalTo(String.valueOf(QUESTION_ID)))
                .withQueryParam(SITE_PARAM.getKey(), equalTo(SITE_PARAM.getValue()))
                .withQueryParam(SORT_PARAM.getKey(), equalTo(SORT_PARAM.getValue()))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowWebClient.fetchQuestionAnswers(QUESTION_ID);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty().hasSize(2);
    }
}

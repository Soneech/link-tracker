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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StackOverflowWebClientTest extends HttpClientTest {
    private Long questionId;

    private StackOverflowWebClient stackOverflowWebClient;

    @BeforeEach
    public void setUp() {
        stackOverflowWebClient = new StackOverflowWebClient(baseUrl);
        questionId = 28892948L;
    }

    @Test
    public void testGettingQuestionInfo() throws IOException {
        File file = ResourceUtils.getFile("classpath:question-response.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty();
        assertThat(response.items().getFirst().id()).isEqualTo(questionId);
    }

    @Test
    public void testResponseOnInvalidQuestionId() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-question-id.json");
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        Long badQuestionId = 12345678910L;

        wireMockServer
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(badQuestionId))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withBody(json)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatExceptionOfType(WebClientResponseException.class)
            .isThrownBy(() -> stackOverflowWebClient.fetchQuestion(badQuestionId));
    }
}

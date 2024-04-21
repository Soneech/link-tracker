package edu.java.bot.client.retry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.ScrapperUnavailableException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ScrapperWebClientTest extends ScrapperClientTest {

    @Autowired
    private ScrapperClient scrapperWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SCENARIO_NAME = "scrapper-scenario";

    private static final Long CHAT_ID = 84984189484L;

    private static final String SUCCESS_REGISTER_CHAT_MESSAGE = "Чат успешно зарегистрирован";

    private static final String SUCCESS_DELETE_CHAT_MESSAGE = "Чат успешно удалён";

    private static final String TEST_LINK = "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    private static final Long LINK_ID = 45698489L;

    private static ListLinksResponse listLinksResponse;

    private static LinkResponse linkResponse;

    @BeforeAll
    public static void setUp() {
        listLinksResponse = new ListLinksResponse(List.of(
            new LinkResponse(123L, URI.create("https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock")),
            new LinkResponse(LINK_ID, URI.create(TEST_LINK))
        ), 2);

        linkResponse = new LinkResponse(LINK_ID, URI.create(TEST_LINK));
    }

    @Test
    public void testSuccessRetryRegistration() {
        String responseBody = "{ \"message\": \"%s\" }".formatted(SUCCESS_REGISTER_CHAT_MESSAGE);

        stubFailedRegistrationState(SCENARIO_NAME, Scenario.STARTED, "state2", 500, CHAT_ID);
        stubFailedRegistrationState(SCENARIO_NAME, "state2", "state3", 502, CHAT_ID);
        stubSuccessRegistrationState(SCENARIO_NAME, "state3", 200, responseBody, CHAT_ID);

        ResponseMessage responseMessage = scrapperWebClient.registerChat(CHAT_ID);
        assertThat(responseMessage).isNotNull();
        assertThat(responseMessage.message()).isEqualTo(SUCCESS_REGISTER_CHAT_MESSAGE);
    }

    @Test
    public void testFailedRetryRegistration() {
        stubFailedRegistrationState(SCENARIO_NAME, Scenario.STARTED, "state2", 500, CHAT_ID);
        stubFailedRegistrationState(SCENARIO_NAME, "state2", "state3", 502, CHAT_ID);
        stubFailedRegistrationState(SCENARIO_NAME, "state3", "state4", 500, CHAT_ID);

        ScrapperUnavailableException exception = assertThrows(ScrapperUnavailableException.class,
                () -> scrapperWebClient.registerChat(CHAT_ID));

        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(500);
    }

    @Test
    public void testSuccessRetryDeleteChat() {
        String responseBody = "{ \"message\": \"%s\" }".formatted(SUCCESS_DELETE_CHAT_MESSAGE);

        stubFailedDeleteChatState(SCENARIO_NAME, Scenario.STARTED, "state2", 503, CHAT_ID);
        stubFailedDeleteChatState(SCENARIO_NAME, "state2", "state3", 503, CHAT_ID);
        stubSuccessDeleteChatState(SCENARIO_NAME, "state3", 200, responseBody, CHAT_ID);

        ResponseMessage response = scrapperWebClient.deleteChat(CHAT_ID);
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(SUCCESS_DELETE_CHAT_MESSAGE);
    }

    @Test
    public void testFailedRetryDeleteChat() {
        stubFailedDeleteChatState(SCENARIO_NAME, Scenario.STARTED, "state2", 503, CHAT_ID);
        stubFailedDeleteChatState(SCENARIO_NAME, "state2", "state3", 500, CHAT_ID);
        stubFailedDeleteChatState(SCENARIO_NAME, "state3", "state4", 503, CHAT_ID);

        ScrapperUnavailableException exception = assertThrows(ScrapperUnavailableException.class,
            () -> scrapperWebClient.deleteChat(CHAT_ID));
        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(503);
    }

    @Test
    public void testSuccessRetryGetLinks() throws JsonProcessingException {
        String responseBody = objectMapper.writeValueAsString(listLinksResponse);

        stubFailedGetLinksState(SCENARIO_NAME, Scenario.STARTED, "state2", 500, CHAT_ID);
        stubSuccessGetLinksState(SCENARIO_NAME, "state2", 200, responseBody, CHAT_ID);

        ListLinksResponse actualResponse = scrapperWebClient.getLinks(CHAT_ID);
        assertThat(actualResponse).isNotNull().isEqualTo(listLinksResponse);
    }

    @Test
    public void testFailedRetryGetLinks() {
        stubFailedGetLinksState(SCENARIO_NAME, Scenario.STARTED, "state2", 504, CHAT_ID);
        stubFailedGetLinksState(SCENARIO_NAME, "state2", "state3", 503, CHAT_ID);
        stubFailedGetLinksState(SCENARIO_NAME, "state3", "state4", 504, CHAT_ID);

        ScrapperUnavailableException exception = assertThrows(ScrapperUnavailableException.class,
            () -> scrapperWebClient.getLinks(CHAT_ID));
        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(504);
    }

    @Test
    public void testSuccessRetryAddLink() throws JsonProcessingException {
        String jsonResponseBody = objectMapper.writeValueAsString(linkResponse);

        stubFailedAddLinkState(SCENARIO_NAME, Scenario.STARTED, "state2", 504, CHAT_ID);
        stubSuccessAddLinkState(SCENARIO_NAME, "state2", 200, jsonResponseBody, CHAT_ID);

        LinkResponse response = scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK));
        assertThat(response).isNotNull().isEqualTo(linkResponse);
    }

    @Test
    public void testFailedRetryAddLink() {
        stubFailedAddLinkState(SCENARIO_NAME, Scenario.STARTED, "state2", 504, CHAT_ID);
        stubFailedAddLinkState(SCENARIO_NAME, "state2", "state3", 504, CHAT_ID);
        stubFailedAddLinkState(SCENARIO_NAME, "state3", "state4", 500, CHAT_ID);

        ScrapperUnavailableException exception = assertThrows(ScrapperUnavailableException.class,
            () -> scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK)));
        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(500);
    }

    @Test
    public void testSuccessRetryDeleteLink() throws JsonProcessingException {
        String jsonResponseBody = objectMapper.writeValueAsString(linkResponse);

        stubFailedDeleteLinkState(SCENARIO_NAME, Scenario.STARTED, "state2", 500, CHAT_ID);
        stubSuccessDeleteLinkState(SCENARIO_NAME, "state2", 200, jsonResponseBody, CHAT_ID);

        LinkResponse response = scrapperWebClient.deleteLink(CHAT_ID, new RemoveLinkRequest(TEST_LINK));
        assertThat(response).isNotNull().isEqualTo(linkResponse);
    }

    @Test
    public void testFailedRetryDeleteLink() {
        stubFailedDeleteLinkState(SCENARIO_NAME, Scenario.STARTED, "state2", 503, CHAT_ID);
        stubFailedDeleteLinkState(SCENARIO_NAME, "state2", "state3", 500, CHAT_ID);
        stubFailedDeleteLinkState(SCENARIO_NAME, "state3", "state4", 500, CHAT_ID);

        ScrapperUnavailableException exception = assertThrows(ScrapperUnavailableException.class,
            () -> scrapperWebClient.deleteLink(CHAT_ID, new RemoveLinkRequest(TEST_LINK)));
        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatusCode().value()).isEqualTo(500);
    }
}

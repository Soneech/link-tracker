package edu.java.bot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.AddedResourceNotExistsException;
import edu.java.bot.exception.NotFoundException;
import edu.java.bot.exception.ResourceUnavailableException;
import edu.java.bot.exception.TooManyRequestsException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class ScrapperWebClientTest extends ScrapperClientTest {

    private static ScrapperWebClient scrapperWebClient;

    private static ObjectMapper objectMapper;

    private static ApiErrorResponse tooManyRequestsApiResponse;

    private static ApiErrorResponse notFoundApiResponse;

    private static final Long CHAT_ID = 84984189484L;

    private static final String SUCCESS_REGISTER_CHAT_MESSAGE = "Чат успешно зарегистрирован";

    private static final String SUCCESS_DELETE_CHAT_MESSAGE = "Чат успешно удалён";

    private static final String TEST_LINK = "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    private static final Long LINK_ID = 45698489L;

    @BeforeAll
    public static void clientSetUp() {
        scrapperWebClient = new ScrapperWebClient(baseUrl);

        List<HttpStatus> retryStatusCodes = List.of(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY);
        Predicate<HttpStatusCode> retryStatusesPredicate = retryStatusCodes::contains;
        scrapperWebClient.setRetryStatusCodes(retryStatusCodes);
        scrapperWebClient.setRetryStatusesPredicate(retryStatusesPredicate);

        objectMapper = new ObjectMapper();

        tooManyRequestsApiResponse = ApiErrorResponse.builder()
            .description("Too Many Requests")
            .code("429 TOO_MANY_REQUESTS")
            .exceptionName("TooManyRequestsException")
            .message("You have exhausted your API Request Quota")
            .stackTrace(Collections.emptyList()).build();

        notFoundApiResponse = ApiErrorResponse.builder()
            .description("Чат не найден")
            .code("404 NOT_FOUND")
            .exceptionName("TelegramChatNotFoundException")
            .message("Чат с таким id не найден")
            .stackTrace(Collections.emptyList()).build();
    }

    @Test
    public void testSuccessRegisterChat() {
        String jsonBody = "{ \"message\": \"%s\" }".formatted(SUCCESS_REGISTER_CHAT_MESSAGE);

        stubForRegisterChat(200, jsonBody, CHAT_ID);

        ResponseMessage response = scrapperWebClient.registerChat(CHAT_ID);
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(SUCCESS_REGISTER_CHAT_MESSAGE);
    }

    @Test
    public void testRegisterChatWithTooManyRequests() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(tooManyRequestsApiResponse);

        stubForRegisterChat(429, jsonBody, CHAT_ID);

        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class,
            () -> scrapperWebClient.registerChat(CHAT_ID));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(tooManyRequestsApiResponse);
    }

    @Test
    public void testSuccessDeleteChat() {
        String jsonBody = "{ \"message\": \"%s\" }".formatted(SUCCESS_DELETE_CHAT_MESSAGE);

        stubForDeleteChat(200, jsonBody, CHAT_ID);

        ResponseMessage response = scrapperWebClient.deleteChat(CHAT_ID);
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo(SUCCESS_DELETE_CHAT_MESSAGE);
    }

    @Test
    public void testDeleteNotExistentChat() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(notFoundApiResponse);

        stubForDeleteChat(404, jsonBody, CHAT_ID);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> scrapperWebClient.deleteChat(CHAT_ID));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(notFoundApiResponse);
    }

    @Test
    public void testDeleteChatWithTooManyRequests() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(tooManyRequestsApiResponse);

        stubForDeleteChat(429, jsonBody, CHAT_ID);

        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class,
            () -> scrapperWebClient.deleteChat(CHAT_ID));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(tooManyRequestsApiResponse);
    }

    @Test
    public void testSuccessGetLinks() throws JsonProcessingException {
        ListLinksResponse expectedResponse = new ListLinksResponse(List.of(
            new LinkResponse(123L, URI.create("https://github.com/wiremock/wiremock")),
            new LinkResponse(LINK_ID, URI.create(TEST_LINK))
        ), 2);

        String jsonBody = objectMapper.writeValueAsString(expectedResponse);

        stubForGetLinks(200, jsonBody, CHAT_ID);

        ListLinksResponse response = scrapperWebClient.getLinks(CHAT_ID);
        assertThat(response).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    public void testGetLinksForNonExistentChat() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(notFoundApiResponse);

        stubForGetLinks(404, jsonBody, CHAT_ID);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> scrapperWebClient.getLinks(CHAT_ID));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(notFoundApiResponse);
    }

    @Test
    public void testGetLinksWithTooManyRequests() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(tooManyRequestsApiResponse);

        stubForGetLinks(429, jsonBody, CHAT_ID);

        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class,
            () -> scrapperWebClient.getLinks(CHAT_ID));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(tooManyRequestsApiResponse);
    }

    @Test
    public void testSuccessAddLink() {
        String jsonBody = "{\"id\": %d, \"uri\": \"%s\"}".formatted(LINK_ID, TEST_LINK);

        stubForAddLink(200, jsonBody, CHAT_ID);

        LinkResponse response = scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK));
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(LINK_ID);
        assertThat(response.uri().toString()).isEqualTo(TEST_LINK);
    }

    @Test
    public void testAddLinkForNonExistentChat() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(notFoundApiResponse);

        stubForAddLink(404, jsonBody, CHAT_ID);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(notFoundApiResponse);
    }

    @Test
    public void testAddLinkWithToManyRequests() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(tooManyRequestsApiResponse);

        stubForAddLink(429, jsonBody, CHAT_ID);

        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class,
            () -> scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(tooManyRequestsApiResponse);
    }

    @Test
    public void testAddLinkToNonExistentResource() throws JsonProcessingException {
        ApiErrorResponse resourceNotExistsResponse = ApiErrorResponse.builder()
            .description("Ресурс не найден")
            .code("418 I_AM_A_TEAPOT")
            .exceptionName("ResourceNotExistsException")
            .message("Ссылка на несуществующий ресурс, либо ресурс был удалён")
            .stackTrace(Collections.emptyList()).build();
        String jsonBody = objectMapper.writeValueAsString(resourceNotExistsResponse);

        stubForAddLink(418, jsonBody, CHAT_ID);

        AddedResourceNotExistsException exception = assertThrows(AddedResourceNotExistsException.class,
            () -> scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(resourceNotExistsResponse);
    }

    @Test
    public void testAddLinkToResourceThatUnavailable() throws JsonProcessingException {
        ApiErrorResponse resourceUnavailableResponse = ApiErrorResponse.builder()
            .description("Ресурс недоступен")
            .code("503 SERVICE_UNAVAILABLE")
            .exceptionName("ResourceUnavailableException")
            .message("Ресурс временно недоступен")
            .stackTrace(Collections.emptyList()).build();
        String jsonBody = objectMapper.writeValueAsString(resourceUnavailableResponse);

        stubForAddLink(503, jsonBody, CHAT_ID);

        ResourceUnavailableException exception = assertThrows(ResourceUnavailableException.class,
            () -> scrapperWebClient.addLink(CHAT_ID, new AddLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(resourceUnavailableResponse);
    }

    @Test
    public void testSuccessDeleteLink() {
        String jsonBody = "{\"id\": %d, \"uri\": \"%s\"}".formatted(LINK_ID, TEST_LINK);

        stubForDeleteLink(200, jsonBody, CHAT_ID);

        LinkResponse response = scrapperWebClient.deleteLink(CHAT_ID, new RemoveLinkRequest(TEST_LINK));
        assertThat(response).isNotNull();
        assertThat(response.uri().toString()).isEqualTo(TEST_LINK);
        assertThat(response.id()).isEqualTo(LINK_ID);
    }

    @Test
    public void testDeleteLinkForNonExistentChat() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(notFoundApiResponse);

        stubForDeleteLink(404, jsonBody, CHAT_ID);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> scrapperWebClient.deleteLink(CHAT_ID, new RemoveLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(notFoundApiResponse);
    }

    @Test
    public void testDeleteLinkWithTooManyRequests() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(tooManyRequestsApiResponse);

        stubForDeleteLink(429, jsonBody, CHAT_ID);

        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class,
            () -> scrapperWebClient.deleteLink(CHAT_ID, new RemoveLinkRequest(TEST_LINK)));
        assertThat(exception.getApiErrorResponse()).isNotNull().isEqualTo(tooManyRequestsApiResponse);
    }
}

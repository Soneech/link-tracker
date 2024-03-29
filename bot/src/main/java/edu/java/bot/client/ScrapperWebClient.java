package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.ApiAddedResourceNotExistsException;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import edu.java.bot.exception.ApiResourceUnavailableException;
import edu.java.bot.exception.ScrapperUnavailableException;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.function.Predicate;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Setter
public class ScrapperWebClient implements ScrapperClient {

    private final WebClient webClient;

    @Value("${api.scrapper.base-url}")
    private String baseUrl;

    @Value("${retry.scrapper.error-status-codes}")
    private List<HttpStatus> errorStatusCodes;

    private Predicate<HttpStatusCode> statusPredicate;

    private static final String TELEGRAM_CHAT_ID_HEADER = "Tg-Chat-Id";

    private static final String LINK_ENDPOINTS_PATH = "/links";

    private static final String TELEGRAM_CHAT_ENDPOINTS_PATH = "/tg-chat/";

    public ScrapperWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public ScrapperWebClient(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @PostConstruct
    public void setPredicate() {
        this.statusPredicate = statusCode -> errorStatusCodes.contains(statusCode);
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public ResponseMessage registerChat(Long chatId) {
        return webClient
            .post().uri(TELEGRAM_CHAT_ENDPOINTS_PATH + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                statusPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot register chat"))
            )
            .bodyToMono(ResponseMessage.class).block();
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public ResponseMessage deleteChat(Long chatId) {
        return webClient
            .delete().uri(TELEGRAM_CHAT_ENDPOINTS_PATH + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .onStatus(
                statusPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot delete chat"))
            )
            .bodyToMono(ResponseMessage.class)
            .block();
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public ListLinksResponse getLinks(Long chatId) {
        return webClient
            .get().uri(LINK_ENDPOINTS_PATH)
            .header(TELEGRAM_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .onStatus(
                statusPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot get chat links"))
            )
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        return webClient
            .post().uri(LINK_ENDPOINTS_PATH)
            .header(TELEGRAM_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .onStatus(
                HttpStatus.I_AM_A_TEAPOT::equals,  // временно
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiAddedResourceNotExistsException::new)
            )
            .onStatus(
                HttpStatus.SERVICE_UNAVAILABLE::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiResourceUnavailableException::new)
            )
            .onStatus(
                statusPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot add link"))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest request) {
        return webClient
            .method(HttpMethod.DELETE).uri(LINK_ENDPOINTS_PATH)
            .header(TELEGRAM_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .onStatus(
                statusPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot delete link"))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }
}

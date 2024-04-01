package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.AddedResourceNotExistsException;
import edu.java.bot.exception.BadRequestException;
import edu.java.bot.exception.NotFoundException;
import edu.java.bot.exception.ResourceUnavailableException;
import edu.java.bot.exception.ScrapperUnavailableException;
import edu.java.bot.exception.TooManyRequestsException;
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

    @Value("${retry.scrapper.retry-status-codes}")
    private List<HttpStatus> retryStatusCodes;

    private Predicate<HttpStatusCode> retryStatusesPredicate;

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
    public void initPredicate() {
        this.retryStatusesPredicate = statusCode -> retryStatusCodes.contains(statusCode);
    }

    @Override
    @Retryable(retryFor = {ScrapperUnavailableException.class, WebClientRequestException.class}, // exponential backoff
               maxAttemptsExpression = "${retry.scrapper.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.scrapper.delay}",
                                  multiplierExpression = "${retry.scrapper.multiplier}"))
    public ResponseMessage registerChat(Long chatId) {
        return webClient
            .post().uri(builder -> builder
                .path(TELEGRAM_CHAT_ENDPOINTS_PATH).path(String.valueOf(chatId)).build())
            .header(TELEGRAM_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BadRequestException::new)
            )
            .onStatus(
                HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(TooManyRequestsException::new)
            )
            .onStatus(
                retryStatusesPredicate,
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
            .delete().uri(builder -> builder
                .path(TELEGRAM_CHAT_ENDPOINTS_PATH).path(String.valueOf(chatId)).build())
            .header(TELEGRAM_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(NotFoundException::new)
            )
            .onStatus(
                HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(TooManyRequestsException::new)
            )
            .onStatus(
                retryStatusesPredicate,
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
                response -> response.bodyToMono(ApiErrorResponse.class).map(BadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(NotFoundException::new)
            )
            .onStatus(
                HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(TooManyRequestsException::new)
            )
            .onStatus(
                retryStatusesPredicate,
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
                response -> response.bodyToMono(ApiErrorResponse.class).map(BadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(NotFoundException::new)
            )
            .onStatus(
                HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(TooManyRequestsException::new)
            )
            .onStatus(
                HttpStatus.I_AM_A_TEAPOT::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(AddedResourceNotExistsException::new)
            )
            .onStatus(
                HttpStatus.SERVICE_UNAVAILABLE::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ResourceUnavailableException::new)
            )
            .onStatus(
                retryStatusesPredicate,
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
                response -> response.bodyToMono(ApiErrorResponse.class).map(BadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(NotFoundException::new)
            )
            .onStatus(
                HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(TooManyRequestsException::new)
            )
            .onStatus(
                retryStatusesPredicate,
                response -> Mono.error(new ScrapperUnavailableException(response.statusCode(), "Cannot delete link"))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }
}

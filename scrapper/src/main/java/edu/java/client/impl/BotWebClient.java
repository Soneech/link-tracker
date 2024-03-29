package edu.java.client.impl;

import edu.java.client.BotClient;
import edu.java.dto.api.response.ApiErrorResponse;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import edu.java.exception.ApiErrorResponseException;
import edu.java.exception.ResourceUnavailableException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

public class BotWebClient implements BotClient {

    private final WebClient webClient;

    @Value("${api.bot.base-url}")
    private String baseUrl;

    @Value("${retry.bot.error-status-codes}")
    private List<HttpStatus> errorStatusCodes;

    private static final Logger LOGGER = LogManager.getLogger();

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public BotWebClient(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override  // constant retry
    @Retryable(retryFor = {ResourceUnavailableException.class, WebClientRequestException.class},
               maxAttemptsExpression = "${retry.bot.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.bot.delay}"))
    public LinkUpdateResponse sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post().uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiErrorResponseException::new))
            .onStatus(
                statusCode -> errorStatusCodes.contains(statusCode),
                response -> Mono.error(new ResourceUnavailableException(response.statusCode()))
            )
            .bodyToMono(LinkUpdateResponse.class)
            .block();
    }

    @Override
    @Recover
    public LinkUpdateResponse recoverSendUpdate(ResourceUnavailableException exception, LinkUpdateRequest request) {
        return handleErrors(exception.getHttpStatusCode().toString(), request);
    }

    @Override
    @Recover
    public LinkUpdateResponse recoverSendUpdate(WebClientRequestException exception, LinkUpdateRequest request) {
        return handleErrors(exception.getMessage(), request);
    }

    @Override
    public LinkUpdateResponse handleErrors(String message, LinkUpdateRequest request) {
        LOGGER.error("Cannot get response from bot: %s; update request: %s".formatted(message, request));
        return new LinkUpdateResponse("Bot unavailable");
    }
}

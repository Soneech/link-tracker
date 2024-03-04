package edu.java.client.impl;

import edu.java.client.BotClient;
import edu.java.dto.api.response.ApiErrorResponse;
import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.dto.bot.LinkUpdateResponse;
import edu.java.exception.ApiErrorResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


public class BotWebClient implements BotClient {
    @Value("${api.bot.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public BotWebClient(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override
    public LinkUpdateResponse sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post().uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiErrorResponseException::new))
            .bodyToMono(LinkUpdateResponse.class)
            .block();
    }
}

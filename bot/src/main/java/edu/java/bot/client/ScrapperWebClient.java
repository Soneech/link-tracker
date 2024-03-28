package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.SuccessMessageResponse;
import edu.java.bot.exception.ApiAddedResourceNotExistsException;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import edu.java.bot.exception.ApiResourceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperWebClient implements ScrapperClient {
    @Value("${api.scrapper.base-url}")
    private String baseUrl;

    private final WebClient webClient;

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

    public SuccessMessageResponse registerChat(Long chatId) {
        return webClient
            .post().uri(TELEGRAM_CHAT_ENDPOINTS_PATH + chatId)
            .retrieve().onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .bodyToMono(SuccessMessageResponse.class).block();
    }

    @Override
    public SuccessMessageResponse deleteChat(Long chatId) {
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
            .bodyToMono(SuccessMessageResponse.class)
            .block();
    }

    @Override
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
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    @Override
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
            .bodyToMono(LinkResponse.class)
            .block();
    }

    @Override
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
            .bodyToMono(LinkResponse.class)
            .block();
    }
}

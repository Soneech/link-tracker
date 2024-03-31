package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.ApiException;
import edu.java.bot.exception.ScrapperUnavailableException;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.reactive.function.client.WebClientRequestException;

public interface ScrapperClient {

    int SERVICE_UNAVAILABLE_STATUS_CODE = 503;

    ResponseMessage registerChat(Long chatId);

    ResponseMessage deleteChat(Long chatId);

    ListLinksResponse getLinks(Long chatId);

    LinkResponse addLink(Long chatId, AddLinkRequest request);

    LinkResponse deleteLink(Long chatId, RemoveLinkRequest request);

    // Теперь правда требуется @Recover метод на каждый бросаемый exception... Возможно можно сделать лучше :\

    @Recover
    default ResponseMessage recoverRegisterOrDelete(ApiException exception, Long chatId) {
        throw exception;
    }

    @Recover
    default ResponseMessage recoverRegisterOrDelete(ScrapperUnavailableException exception, Long chatId) {
        throw exception;
    }

    @Recover
    default ResponseMessage recoverRegisterOrDelete(WebClientRequestException exception, Long chatId) {
        throw getScrapperException(exception.getMessage());
    }

    @Recover
    default ListLinksResponse recoverGetLinks(ApiException exception, Long chatId) {
        throw exception;
    }

    @Recover
    default ListLinksResponse recoverGetLinks(ScrapperUnavailableException exception, Long chatId) {
        throw exception;
    }

    @Recover
    default ListLinksResponse recoverGetLinks(WebClientRequestException exception, Long chatId) {
        throw getScrapperException(exception.getMessage());
    }

    @Recover
    default LinkResponse recoverAddLink(ApiException exception, Long chatId, AddLinkRequest request) {
        throw exception;
    }

    @Recover
    default LinkResponse recoverAddLink(ScrapperUnavailableException exception, Long chatId, AddLinkRequest request) {
        throw exception;
    }

    @Recover
    default LinkResponse recoverAddLink(WebClientRequestException exception, Long chatId, AddLinkRequest request) {
        throw getScrapperException(exception.getMessage());
    }

    @Recover
    default LinkResponse recoverDeleteLink(ApiException exception, Long chatId, RemoveLinkRequest request) {
        throw exception;
    }

    @Recover
    default LinkResponse recoverDeleteLink(ScrapperUnavailableException exception, Long chatId,
        RemoveLinkRequest request) {

        throw exception;
    }

    @Recover
    default LinkResponse recoverDeleteLink(WebClientRequestException exception, Long chatId,
        RemoveLinkRequest request) {

        throw getScrapperException(exception.getMessage());
    }

    default ScrapperUnavailableException getScrapperException(String message) {
        return new ScrapperUnavailableException(HttpStatusCode.valueOf(SERVICE_UNAVAILABLE_STATUS_CODE),
            "Scrapper unavailable: %s".formatted(message));
    }
}

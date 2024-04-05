package edu.java.controller;

import edu.java.dto.api.request.AddLinkRequest;
import edu.java.dto.api.request.RemoveLinkRequest;
import edu.java.dto.api.response.ApiErrorResponse;
import edu.java.dto.api.response.LinkResponse;
import edu.java.dto.api.response.ListLinksResponse;
import edu.java.dto.api.response.ResponseMessage;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// for swagger doc
public interface ApiController {

    @ApiResponse(responseCode = "200", description = "Чат зарегистрирован",
                 content = @Content(schema = @Schema(implementation = ResponseMessage.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса, либо чат уже зарегистрирован",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Превышен лимит запросов",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseMessage registerChat(@PathVariable("id") Long chatId);


    @ApiResponse(responseCode = "200", description = "Чат успешно удалён",
                 content = @Content(schema = @Schema(implementation = ResponseMessage.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Превышен лимит запросов",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseMessage deleteChat(@PathVariable("id") Long chatId);


    @ApiResponse(responseCode = "200", description = "Ссылки успешно получены",
                 content = @Content(schema = @Schema(implementation = ListLinksResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Превышен лимит запросов",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long chatId);


    @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена",
                 content = @Content(schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "418", description = "Ссылка на несуществующий ресурс",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "Ссылка на ресурс, который временно недоступен",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Превышен лимит запросов",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid AddLinkRequest request);


    @ApiResponse(responseCode = "200", description = "Ссылка успешно удалена",
                 content = @Content(schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует, либо ссылка не найдена",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Превышен лимит запросов",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    LinkResponse deleteLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid RemoveLinkRequest request);
}

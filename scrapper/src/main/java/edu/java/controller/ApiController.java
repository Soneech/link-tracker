package edu.java.controller;

import edu.java.dto.api.request.AddLinkRequest;
import edu.java.dto.api.request.RemoveLinkRequest;
import edu.java.dto.api.response.ApiErrorResponse;
import edu.java.dto.api.response.LinkResponse;
import edu.java.dto.api.response.ListLinksResponse;
import edu.java.dto.api.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// for swagger doc
public interface ApiController {
    String MEDIA_TYPE = "application/json";

    @ApiResponse(responseCode = "200", description = "Чат зарегистрирован",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = SuccessResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    SuccessResponse registerChat(@PathVariable("id") Long id);


    @ApiResponse(responseCode = "200", description = "Чат успешно удалён",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = SuccessResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    SuccessResponse deleteChat(@PathVariable("id") Long id);


    @ApiResponse(responseCode = "200", description = "Ссылки успешно получены",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ListLinksResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long chatId);


    @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid AddLinkRequest request);


    @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует, либо ссылка не найдена",
                 content =
                 @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = ApiErrorResponse.class)))
    LinkResponse deleteLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid RemoveLinkRequest request);
}

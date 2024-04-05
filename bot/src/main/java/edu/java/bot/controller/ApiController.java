package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.ResponseMessage;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

// for swagger doc
public interface ApiController {

    @ApiResponse(responseCode = "200", description = "Обновление обработано",
                 content = @Content(schema = @Schema(implementation = ResponseMessage.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseMessage handleUpdate(@RequestBody @Valid LinkUpdateRequest request);
}

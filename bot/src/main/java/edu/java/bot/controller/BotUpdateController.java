package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.service.UpdateService;
import io.micrometer.core.instrument.Counter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BotUpdateController implements ApiController {

    private final UpdateService updateService;

    private final Counter processedUpdatesCounter;

    @Override
    @PostMapping("/updates")
    public ResponseMessage handleUpdate(@RequestBody @Valid LinkUpdateRequest request) {
        updateService.processUpdate(request);

        processedUpdatesCounter.increment();
        return new ResponseMessage("Обновление обработано");
    }
}

package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.dto.response.SuccessUpdateResponse;
import edu.java.bot.service.UpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BotUpdateController {
    private final UpdateService updateService;

    @PostMapping("/updates")
    public SuccessUpdateResponse handleUpdate(@RequestBody @Valid LinkUpdateRequest request) {
        updateService.addUpdate(request);
        return new SuccessUpdateResponse("Обновление обработано");
    }
}

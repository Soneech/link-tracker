package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.LinkBot;
import edu.java.bot.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final LinkBot linkBot;

    public void processUpdate(LinkUpdateRequest updateRequest) {
        String message = "Новая информация по ссылке\n%s: %s"
            .formatted(updateRequest.url(), updateRequest.description());

        updateRequest.telegramChatIds().forEach((telegramId) ->
            linkBot.execute(new SendMessage(telegramId, message)));
    }
}

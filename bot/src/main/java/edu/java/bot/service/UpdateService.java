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
        updateRequest.telegramChatIds().forEach((telegramId) ->
            linkBot.execute(new SendMessage(telegramId, "Доступно обновление по ссылке:\n%s\n\n%s"
                .formatted(updateRequest.url(), updateRequest.updatesDescription()))));
    }
}

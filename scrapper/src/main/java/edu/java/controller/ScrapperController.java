package edu.java.controller;

import edu.java.dto.api.request.AddLinkRequest;
import edu.java.dto.api.request.RemoveLinkRequest;
import edu.java.dto.api.response.LinkResponse;
import edu.java.dto.api.response.ListLinksResponse;
import edu.java.dto.api.response.SuccessResponse;
import edu.java.dto.bot.request.ChatRegistrationRequest;
import edu.java.mapper.DefaultObjectMapper;
import edu.java.model.Link;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScrapperController implements ApiController {
    private final ChatService chatService;

    private final LinkService linkService;

    private final DefaultObjectMapper mapper;

    @Override
    @PostMapping("/tg-chat")
    public SuccessResponse registerChat(@RequestBody ChatRegistrationRequest request) {
        chatService.registerChat(mapper.convertToChat(request));
        return new SuccessResponse("Чат с id %d успешно зарегистрирован.".formatted(request.id()));
    }

    @Override
    @DeleteMapping("/tg-chat/{id}")
    public SuccessResponse deleteChat(@PathVariable("id") Long id) {
        chatService.unregisterChat(id);
        return new SuccessResponse("Чат с id %d успешно удалён.".formatted(id));
    }

    @Override
    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        List<Link> userLinks = linkService.getUserLinks(chatId);
        return mapper.mapToListLinksResponse(userLinks);
    }

    @Override
    @PostMapping("/links")
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid AddLinkRequest request) {

        Link link = mapper.convertToLink(request);
        Link addedLink = linkService.addLink(chatId, link);
        return mapper.convertToLinkResponse(addedLink);
    }

    @Override
    @DeleteMapping("/links")
    public LinkResponse deleteLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid RemoveLinkRequest request) {

        Link link = mapper.convertToLink(request);
        Link removedLink = linkService.deleteLink(chatId, link);
        return mapper.convertToLinkResponse(removedLink);
    }
}

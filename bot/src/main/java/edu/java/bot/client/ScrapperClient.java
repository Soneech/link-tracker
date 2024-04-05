package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.dto.response.ResponseMessage;

public interface ScrapperClient {

    ResponseMessage registerChat(Long chatId);

    ResponseMessage deleteChat(Long chatId);

    ListLinksResponse getLinks(Long chatId);

    LinkResponse addLink(Long chatId, AddLinkRequest request);

    LinkResponse deleteLink(Long chatId, RemoveLinkRequest request);
}

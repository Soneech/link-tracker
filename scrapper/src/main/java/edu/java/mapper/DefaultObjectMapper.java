package edu.java.mapper;

import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.model.Link;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultObjectMapper {

    public ListLinksResponse mapToListLinksResponse(List<Link> links) {
        List<LinkResponse> linkResponses = mapToListWithLinkResponses(links);
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public List<LinkResponse> mapToListWithLinkResponses(List<Link> links) {
        return links.stream().map(link -> new LinkResponse(link.getId(), link.getUri())).toList();
    }

    public Link convertToLink(AddLinkRequest request) {
        return new Link(URI.create(request.link()));
    }

    public Link convertToLink(RemoveLinkRequest request) {
        return new Link(URI.create(request.link()));
    }

    public LinkResponse convertToLinkResponse(Link link) {
        return new LinkResponse(link.getId(), link.getUri());
    }
}

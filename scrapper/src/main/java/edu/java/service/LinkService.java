package edu.java.service;

import edu.java.model.Link;
import java.util.List;

public interface LinkService {
    List<Link> getUserLinks(long chatId);

    Link addLink(long chatId, Link link);

    Link deleteLink(long chatId, Link link);
}

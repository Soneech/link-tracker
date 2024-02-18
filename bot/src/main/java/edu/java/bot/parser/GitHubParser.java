package edu.java.bot.parser;

import edu.java.bot.website.WebsiteInfo;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class GitHubParser implements LinkParser {
    @Override
    public boolean parseLink(URI uri) {
        String host = uri.getHost();
        return WebsiteInfo.GITHUB.getDomain().equals(host);
    }
}

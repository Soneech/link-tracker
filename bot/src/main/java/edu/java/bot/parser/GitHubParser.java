package edu.java.bot.parser;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class GitHubParser implements LinkParser {
    private final Pattern gitHubPatter =
        Pattern.compile("^(https)(://)(github.com)/([\\w\\-\\d]+)/([\\w\\-._\\d]+)(/)*");

    @Override
    public boolean isLinkCorrect(String url) {
        return gitHubPatter.matcher(url).matches();
    }
}

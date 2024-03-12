package edu.java.bot.parser;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowParser implements LinkParser {
    private final Pattern stackOverflowPattern =
        Pattern.compile("^(https)(://)(stackoverflow.com)/(questions)/(\\d+)(/)*([\\w\\-]*)(/)*");

    @Override
    public boolean isLinkCorrect(String url) {
        return stackOverflowPattern.matcher(url).matches();
    }
}


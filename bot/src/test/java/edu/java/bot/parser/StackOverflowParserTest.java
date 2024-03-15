package edu.java.bot.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StackOverflowParserTest extends LinkParserTest {
    private StackOverflowParser stackOverflowParser;

    private static final String FIRST_STACK_OVERFLOW_URL =
        "https://stackoverflow.com/questions/26318569/unfinished-stubbing-detected-in-mockito";
    private static final String SECOND_STACK_OVERFLOW_URL =
        "https://stackoverflow.com/questions/66696828/how-to-use-configurationproperties-with-records";

    private static final String FIRST_RANDOM_URL = "https://fintech.tinkoff.ru/study/";
    private static final String SECOND_RANDOM_URL = "https://github.com/tonsky/FiraCode";

    @BeforeEach
    public void setUp() {
        stackOverflowParser = new StackOverflowParser();
    }

    @Test
    @Override
    public void testWithSupportedWebserviceLink() {
        assertThat(stackOverflowParser.isLinkCorrect(FIRST_STACK_OVERFLOW_URL)).isTrue();
        assertThat(stackOverflowParser.isLinkCorrect(SECOND_STACK_OVERFLOW_URL)).isTrue();
    }

    @Test
    @Override
    public void testWithUnsupportedWebserviceLink() {
        assertThat(stackOverflowParser.isLinkCorrect(FIRST_RANDOM_URL)).isFalse();
        assertThat(stackOverflowParser.isLinkCorrect(SECOND_RANDOM_URL)).isFalse();
    }
}

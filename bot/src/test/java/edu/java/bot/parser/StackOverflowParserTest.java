package edu.java.bot.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(stackOverflowParser.isLinkCorrect(FIRST_STACK_OVERFLOW_URL));
        assertTrue(stackOverflowParser.isLinkCorrect(SECOND_STACK_OVERFLOW_URL));
    }

    @Test
    @Override
    public void testWithUnsupportedWebserviceLink() {
        assertFalse(stackOverflowParser.isLinkCorrect(FIRST_RANDOM_URL));
        assertFalse(stackOverflowParser.isLinkCorrect(SECOND_RANDOM_URL));
    }
}

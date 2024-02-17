package edu.java.bot.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends CommandTest {
    @InjectMocks
    private UntrackCommand untrackCommand;

    @Test
    @Override
    void testThatReturnedCommandTypeIsCorrect() {
        assertThat(untrackCommand.type()).isEqualTo(CommandInfo.UNTRACK.getType());
    }

    @Test
    @Override
    void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(untrackCommand.description()).isEqualTo(CommandInfo.UNTRACK.getDescription());
    }
}

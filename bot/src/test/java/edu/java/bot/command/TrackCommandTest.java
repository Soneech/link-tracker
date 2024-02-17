package edu.java.bot.command;

import edu.java.bot.command.chain.impl.track.TrackCommandStep;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest extends CommandTest {
    @InjectMocks
    private TrackCommand trackCommand;

    @Spy
    private List<TrackCommandStep> chain;

    @Test
    @Override
    void testThatReturnedCommandTypeIsCorrect() {
        assertThat(trackCommand.type()).isEqualTo(CommandInfo.TRACK.getType());
    }

    @Test
    @Override
    void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(trackCommand.description()).isEqualTo(CommandInfo.TRACK.getDescription());
    }
}

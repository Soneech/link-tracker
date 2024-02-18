package edu.java.bot.command.chain.impl.defaults;

import edu.java.bot.command.chain.impl.track.TrackCommandStep;
import edu.java.bot.command.chain.impl.untrack.UntrackCommandStep;

public interface DefaultCommandStep extends TrackCommandStep, UntrackCommandStep { }

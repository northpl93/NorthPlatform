package pl.north93.northplatform.api.minigame.shared.api.utils;

import static java.text.MessageFormat.format;


import pl.north93.northplatform.api.minigame.shared.api.GamePhase;

public class InvalidGamePhaseException extends RuntimeException
{
    private final GamePhase current;
    private final GamePhase expected;

    public InvalidGamePhaseException(final GamePhase current, final GamePhase expected)
    {
        super(format("Arena is in invalid game phase: {0}, expected: {1}.", current, expected));
        this.current = current;
        this.expected = expected;
    }

    public GamePhase getCurrent()
    {
        return this.current;
    }

    public GamePhase getExpected()
    {
        return this.expected;
    }

    public static void checkGamePhase(final GamePhase actual, final GamePhase expected)
    {
        if (actual != expected)
        {
            throw new InvalidGamePhaseException(actual, expected);
        }
    }
}

package pl.north93.northplatform.api.minigame.shared.api.utils;

import static java.text.MessageFormat.format;


import pl.north93.northplatform.api.minigame.shared.api.GamePhase;

public class InvalidGamePhaseException extends RuntimeException
{
    private final GamePhase current;

    public InvalidGamePhaseException(final GamePhase current)
    {
        super(format("Arena is in invalid game phase ({0}).", current));
        this.current = current;
    }

    public GamePhase getCurrent()
    {
        return this.current;
    }

    public static void checkGamePhase(final GamePhase actual, final GamePhase expected)
    {
        if (actual != expected)
        {
            throw new InvalidGamePhaseException(actual);
        }
    }
}

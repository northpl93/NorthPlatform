package pl.north93.zgame.api.global.exceptions;

import java.util.UUID;

public class PlayerNotFoundException extends Exception
{
    private final String playerName;

    public String getPlayerName()
    {
        return this.playerName;
    }

    public PlayerNotFoundException(final String nick)
    {
        super("Player with nickname " + nick + " not found.");
        this.playerName = nick;
    }

    public PlayerNotFoundException(final UUID uuid)
    {
        super("Player with uuid " + uuid + " not found.");
        this.playerName = uuid.toString();
    }
}

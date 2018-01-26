package pl.north93.zgame.api.global.exceptions;

import java.util.UUID;

import pl.north93.zgame.api.global.network.players.Identity;

public class PlayerNotFoundException extends Exception
{
    public PlayerNotFoundException(final String nick)
    {
        super("Player with nickname " + nick + " not found.");
    }

    public PlayerNotFoundException(final UUID uuid)
    {
        super("Player with uuid " + uuid + " not found.");
    }

    public PlayerNotFoundException(final Identity identity)
    {
        super("Player with Identity " + identity.getNick() + "/" + identity.getUuid() + " not found");
    }
}

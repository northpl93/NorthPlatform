package pl.north93.zgame.api.global.network.players;

import static java.text.MessageFormat.format;

/**
 * Wyjątek rzucany gdy nie udało się znaleźć gracza o podanym Identity
 * w bazach danych.
 */
public class PlayerNotFoundException extends RuntimeException
{
    public PlayerNotFoundException(final Identity identity)
    {
        super(generateMessage(identity));
    }

    private static String generateMessage(final Identity identity)
    {
        if (! identity.isValid())
        {
            return "Invalid Identity";
        }
        else if (identity.getNick() != null && identity.getUuid() != null)
        {
            return format("Player with nick {0} and uuid {1} not found", identity.getNick(), identity.getUuid());
        }
        else if (identity.getNick() != null)
        {
            return format("Player with nick {0} not found", identity.getNick());
        }
        return format("Player with uuid {1} not found", identity.getUuid());
    }
}

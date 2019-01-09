package pl.north93.northplatform.api.global.network.players;

import static java.text.MessageFormat.format;

/**
 * Wyjątek rzucany gdy oczekiwaliśmy, że gracz jest online, a ten
 * jednak jest offline.
 */
public class PlayerOfflineException extends RuntimeException
{
    public PlayerOfflineException(final IPlayer player)
    {
        super(format("Player with uuid {0} and nick {1} is offline", player.getUuid(), player.getLatestNick()));
    }
}

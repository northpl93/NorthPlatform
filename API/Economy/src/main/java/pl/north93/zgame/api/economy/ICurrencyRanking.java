package pl.north93.zgame.api.economy;

import java.util.Set;
import java.util.UUID;

public interface ICurrencyRanking
{
    long getPosition(UUID playerId);

    Set<UUID> getTopPlayers(final int count);
}

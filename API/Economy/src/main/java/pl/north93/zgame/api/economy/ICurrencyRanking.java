package pl.north93.zgame.api.economy;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

public interface ICurrencyRanking
{
    long getPosition(UUID playerId);

    Set<Pair<UUID, Long>> getTopPlayers(final int count);
}

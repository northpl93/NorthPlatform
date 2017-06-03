package pl.north93.zgame.api.economy.impl.shared;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.economy.ICurrencyRanking;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.SortedSet;

public class CurrencyRankingImpl implements ICurrencyRanking
{
    private final String currencyName;
    @Inject
    private IObservationManager observationManager;
    private SortedSet<UUID>     ranking;

    public CurrencyRankingImpl(final String currencyName)
    {
        this.currencyName = currencyName;
    }

    @PostInject
    private void postInject()
    {
        this.ranking = this.observationManager.getSortedSet("currank:" + this.currencyName);
    }

    @Override
    public long getPosition(final UUID playerId)
    {
        return this.ranking.getRevRank(playerId);
    }

    @Override
    public Set<UUID> getTopPlayers(final int count)
    {
        final Set<String> range = this.ranking.getRevRange(0, count - 1);
        final Set<UUID> topPlayers = new LinkedHashSet<>(range.size(), 0.001f);
        for (final String stringUuid : range)
        {
            topPlayers.add(UUID.fromString(stringUuid));
        }
        return topPlayers;
    }

    @Override
    public Set<Pair<UUID, Long>> getTopPlayersMoney(final int count)
    {
        final Set<Pair<String, Long>> range = this.ranking.getRevRangeWithScores(0, count - 1);
        final Set<Pair<UUID, Long>> topPlayers = new LinkedHashSet<>(range.size(), 0.001f);
        for (final Pair<String, Long> element : range)
        {
            topPlayers.add(Pair.of(UUID.fromString(element.getKey()), element.getValue()));
        }
        return topPlayers;
    }

    void update(final UUID playerId, final double amount)
    {
        this.ranking.add(playerId, amount);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencyName", this.currencyName).toString();
    }
}

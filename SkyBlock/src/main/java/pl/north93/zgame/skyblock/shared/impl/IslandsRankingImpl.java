package pl.north93.zgame.skyblock.shared.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.SortedSet;
import pl.north93.zgame.skyblock.shared.api.IIslandsRanking;

public final class IslandsRankingImpl implements IIslandsRanking
{
    @Inject
    private IObservationManager observationManager;
    private SortedSet<UUID>     ranking;

    @PostInject
    private void postInject()
    {
        this.ranking = this.observationManager.getSortedSet("skyrank");
    }

    @Override
    public void setPoints(final UUID islandId, final double points)
    {
        this.ranking.add(islandId, points);
    }

    @Override
    public double getPoints(final UUID islandId)
    {
        return this.ranking.get(islandId);
    }

    @Override
    public long getPosition(final UUID islandId)
    {
        return this.ranking.getRevRank(islandId);
    }

    @Override
    public void removeIsland(final UUID islandId)
    {
        this.ranking.remove(islandId);
    }

    @Override
    public Set<UUID> getTopIslands(final int count)
    {
        final Set<String> range = this.ranking.getRevRange(0, count - 1);
        final Set<UUID> topIslands = new LinkedHashSet<>(range.size(), 0.001f);
        for (final String stringUuid : range)
        {
            topIslands.add(UUID.fromString(stringUuid));
        }
        return topIslands;
    }

    @Override
    public void clearRanking()
    {
        this.ranking.clear();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("ranking", this.ranking).toString();
    }
}

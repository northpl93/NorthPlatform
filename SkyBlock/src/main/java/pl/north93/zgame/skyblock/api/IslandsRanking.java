package pl.north93.zgame.skyblock.api;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.SortedSet;

public final class IslandsRanking
{
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;
    private SortedSet<UUID>     ranking;

    @PostInject
    private void postInject()
    {
        this.ranking = this.observationManager.getSortedSet("skyrank");
    }

    public void setPoints(final UUID islandId, final double points)
    {
        this.ranking.add(islandId, points);
    }

    public double getPoints(final UUID islandId)
    {
        return this.ranking.get(islandId);
    }

    public long getPosition(final UUID islandId)
    {
        return this.ranking.getRevRank(islandId);
    }

    public void removeIsland(final UUID islandId)
    {
        this.ranking.remove(islandId);
    }

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

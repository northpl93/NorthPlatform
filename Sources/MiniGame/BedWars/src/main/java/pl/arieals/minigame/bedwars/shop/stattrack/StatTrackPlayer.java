package pl.arieals.minigame.bedwars.shop.stattrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StatTrackPlayer
{
    private final Player bukkitPlayer;
    private final List<TrackedWeapon> enabledTrackers;
    private final Map<StatTrackCacheKey, Long> cache = new HashMap<>(8);

    public StatTrackPlayer(final Player bukkitPlayer, final List<TrackedWeapon> enabledTrackers)
    {
        this.bukkitPlayer = bukkitPlayer;
        this.enabledTrackers = enabledTrackers;
    }

    public Player getBukkitPlayer()
    {
        return this.bukkitPlayer;
    }

    public boolean isEnabled(final TrackedWeapon weapon)
    {
        return this.enabledTrackers.contains(weapon);
    }

    public synchronized long getCachedStatistic(final TrackedStatistic statistic, final TrackedWeapon weapon)
    {
        return this.cache.getOrDefault(new StatTrackCacheKey(weapon, statistic), 0L);
    }

    public synchronized void updateCache(final TrackedStatistic statistic, final TrackedWeapon weapon, final Long newValue)
    {
        this.cache.put(new StatTrackCacheKey(weapon, statistic), newValue);
    }
}

final class StatTrackCacheKey
{
    private final TrackedWeapon weapon;
    private final TrackedStatistic statistic;

    public StatTrackCacheKey(final TrackedWeapon weapon, final TrackedStatistic statistic)
    {
        this.weapon = weapon;
        this.statistic = statistic;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final StatTrackCacheKey that = (StatTrackCacheKey) o;

        return this.weapon == that.weapon && this.statistic == that.statistic;
    }

    @Override
    public int hashCode()
    {
        int result = this.weapon.hashCode();
        result = 31 * result + this.statistic.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("weapon", this.weapon).append("statistic", this.statistic).toString();
    }
}
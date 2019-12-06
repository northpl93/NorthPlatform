package pl.arieals.minigame.bedwars.shop.stattrack;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@ToString
@EqualsAndHashCode
@AllArgsConstructor
final class StatTrackCacheKey
{
    private final TrackedWeapon weapon;
    private final TrackedStatistic statistic;
}
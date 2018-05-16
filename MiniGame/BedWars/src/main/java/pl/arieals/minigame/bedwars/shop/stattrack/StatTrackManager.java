package pl.arieals.minigame.bedwars.shop.stattrack;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Locale;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class StatTrackManager
{
    @Inject
    private IStatisticsManager statisticsManager;
    @Inject
    private StatTrackItems     statTrackItems;

    @Bean
    private StatTrackManager()
    {
    }

    public void preCacheData(final StatTrackPlayer playerData)
    {
        final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(playerData.getBukkitPlayer().getUniqueId());
        for (final TrackedWeapon trackedWeapon : TrackedWeapon.values())
        {
            if (! playerData.isEnabled(trackedWeapon))
            {
                continue;
            }

            for (final TrackedStatistic trackedStatistic : TrackedStatistic.values())
            {
                final IStatistic<NumberUnit> statistic = this.getStatistic(trackedStatistic, trackedWeapon);
                holder.getBest(statistic).whenComplete((result, throwable) ->
                {
                    if (result == null)
                    {
                        return;
                    }

                    playerData.updateCache(trackedStatistic, trackedWeapon, result.getValue().getValue());
                });
            }
        }
    }

    public void bumpStatistic(final Player player, final TrackedStatistic statistic, final ItemStack tool)
    {
        final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(player.getUniqueId());
        this.bumpGlobalStatistic(holder);

        final TrackedWeapon weapon = TrackedWeapon.getByMaterial(tool.getType());
        if (weapon == null)
        {
            return;
        }

        final StatTrackPlayer playerData = getPlayerData(player, StatTrackPlayer.class);
        if (playerData == null || !playerData.isEnabled(weapon))
        {
            return;
        }

        final IStatistic<NumberUnit> statisticSystem = this.getStatistic(statistic, weapon);
        holder.increment(statisticSystem, new NumberUnit(1L)).whenComplete((result, throwable) ->
        {
            final long newValue = result.getValue().getValue() + 1;
            playerData.updateCache(statistic, weapon, newValue);
            this.statTrackItems.updateWeapons(player, weapon);
        });
    }

    public IStatistic<NumberUnit> getStatistic(final TrackedStatistic statistic, final TrackedWeapon weapon)
    {
        final String statName = statistic.name().toLowerCase(Locale.ROOT);
        final String weaponName = weapon.name().toLowerCase(Locale.ROOT);
        final String statId = "bedwars/stattrak/" + statName + "/" + weaponName;
        return new HigherNumberBetterStatistic(statId);
    }

    private void bumpGlobalStatistic(final IStatisticHolder holder)
    {
        final HigherNumberBetterStatistic killsStat = new HigherNumberBetterStatistic("bedwars/kills");
        holder.increment(killsStat, new NumberUnit(1L));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

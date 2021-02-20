package pl.north93.northplatform.minigame.goldhunter.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.effect.BetrayalEffect;
import pl.north93.northplatform.minigame.goldhunter.effect.ShadowEffect;
import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class StatsTracker implements ITickable
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;

    @Inject
    private static ITickableManager tickableManager;

    @Inject
    private static IStatisticsManager statisticsManager; // todo ogarnac to sensownie
    
    private final GoldHunterPlayer player;
    
    private int kills;
    private int assists;
    private int deaths;
    
    private final Deque<DamagerEntry> lastDamagers = new ArrayDeque<>();
    
    private double totalDamage;
    
    public StatsTracker(GoldHunterPlayer player)
    {
        this.player = player;
        
        tickableManager.addTickableObject(this);
    }
    
    public GoldHunterPlayer getKiller()
    {
        return Optional.ofNullable(lastDamagers.peekFirst()).map(entry -> entry.player).orElse(null);
    }
    
    public Collection<GoldHunterPlayer> getLastDamagers()
    {
        return getLastDamagersStream().collect(Collectors.toCollection(ArrayList::new));
    }
    
    public Collection<GoldHunterPlayer> getKillerAssistants()
    {
        return getLastDamagersStream().skip(1).collect(Collectors.toCollection(ArrayList::new));
    }
    
    private Stream<GoldHunterPlayer> getLastDamagersStream()
    {
        return lastDamagers.stream().map(entry -> entry.player);
    }
    
    public void onDamagePlayer(GoldHunterPlayer damaged, double damageValue)
    {
        logger.debug("{} damaged {} with {}", this.player, damaged, damageValue);
        
        totalDamage += damageValue;
        
        Deque<DamagerEntry> lastDamagers = damaged.getStatsTracker().lastDamagers;
        lastDamagers.removeIf(entry -> entry.player == this.player);
        lastDamagers.push(new DamagerEntry(player));
        
        player.getEffectTracker().removeEffect(BetrayalEffect.class);
        player.getEffectTracker().removeEffect(ShadowEffect.class);
        
        logger.debug("{} lastdamagers: {}", damaged, lastDamagers);
    }
    
    public void onDie()
    {
        logger.debug("{}'s StatsTracker#die()", player);
        
        incrementDeaths();
        
        GoldHunterPlayer killer = getKiller();
        if ( killer != null )
        {
            killer.getStatsTracker().incrementKills();
        }
        
        getKillerAssistants().forEach(player -> player.getStatsTracker().incrementAssists());
        
        lastDamagers.clear();
    }

    public void onWin()
    {
        logger.debug("{} StatTracker#onWin()", player);

        final IStatisticHolder holder = statisticsManager.getPlayerHolder(player.getPlayer().getUniqueId());
        holder.incrementRecord(new HigherNumberBetterStatistic("goldhunter/wins"), new NumberUnit(1L));
    }
    
    public void onChestDestroy()
    {
        logger.debug("{} StatTracker#onChestDestroy()", player);
        
        player.addReward("chests", player.getCurrentClass().getRewardsInfo().getChestDestroyReward());
        IStatisticHolder statisticHolder = statisticsManager.getPlayerHolder(player.getPlayer().getUniqueId());
        statisticHolder.incrementRecord(new HigherNumberBetterStatistic("goldhunter/chestsDestroyed"), new NumberUnit(1L));
    }
    
    private void incrementKills()
    {
        kills++;
        player.addReward("kills", player.getCurrentClass().getRewardsInfo().getKillReward());
        updateScoreboardAndDisplayName();

        final IStatisticHolder holder = statisticsManager.getPlayerHolder(player.getPlayer().getUniqueId());
        holder.incrementRecord(new HigherNumberBetterStatistic("goldhunter/kills"), new NumberUnit(1L));
    }
    
    private void incrementDeaths()
    {
        deaths++;
        updateScoreboardAndDisplayName();
    }
    
    private void incrementAssists()
    {
        assists++;
        player.addReward("assists", player.getCurrentClass().getRewardsInfo().getAssistReward());
        updateScoreboardAndDisplayName();

        final IStatisticHolder holder = statisticsManager.getPlayerHolder(player.getPlayer().getUniqueId());
        holder.incrementRecord(new HigherNumberBetterStatistic("goldhunter/assists"), new NumberUnit(1L));
    }
    
    private void updateScoreboardAndDisplayName()
    {
        player.getScoreboardContext().set(ImmutableMap.of("stats", getStatsString()));
        player.updateDisplayName();
    }
    
    public void clear()
    {
        kills = 0;
        assists = 0;
        deaths = 0;
        
        lastDamagers.clear();
        
        totalDamage = 0;
        
        if ( player.isIngame() )
        {
            updateScoreboardAndDisplayName();
        }
    }
    
    public int getKills()
    {
        return kills;
    }
    
    public int getAssists()
    {
        return assists;
    }
    
    public int getDeaths()
    {
        return deaths;
    }
    
    public double getTotalDamage()
    {
        return totalDamage;
    }
    
    public String getStatsString()
    {
        return getKills() + "/" + getAssists() + "/" + getDeaths();
    }
    
    @Tick
    private void cleanupLastDamagers()
    {
        lastDamagers.removeIf(lastDamage -> Math.abs(lastDamage.tick - MinecraftServer.currentTick) > 100);
    }
    
    private class DamagerEntry implements Comparable<DamagerEntry>
    {
        private final GoldHunterPlayer player;
        private final int tick;
        
        private DamagerEntry(GoldHunterPlayer player)
        {
            this.player = player;
            this.tick = MinecraftServer.currentTick;
        }
        
        @Override
        public int compareTo(DamagerEntry other)
        {
            return other.tick - this.tick;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if ( this == obj )
            {
                return true;
            }
            
            if ( !( obj instanceof DamagerEntry ) )
            {
                return false;
            }
            
            DamagerEntry other = (DamagerEntry) obj;
            return this.player.equals(other.player) && this.tick == other.tick;
        }
        
        @Override
        public int hashCode()
        {
            int result = 31;
            result = 31 * result + player.hashCode();
            result = 31 * result + Integer.hashCode(tick);
            return result;
        }
        
        @Override
        public String toString()
        {
            return player.toString() + "@" + tick;
        }
    }
}

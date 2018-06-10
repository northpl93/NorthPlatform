package pl.arieals.minigame.goldhunter.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.effect.BetrayalEffect;
import pl.arieals.minigame.goldhunter.effect.ShadowEffect;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

public class StatsTracker implements ITickable
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static ITickableManager tickableManager;
    
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
    
    private void incrementKills()
    {
        kills++;
        player.addReward("kills", 2);
        updateScoreboardAndDisplayName();
    }
    
    private void incrementDeaths()
    {
        deaths++;
        updateScoreboardAndDisplayName();
    }
    
    private void incrementAssists()
    {
        assists++;
        player.addReward("assists", 1.5);
        updateScoreboardAndDisplayName();
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

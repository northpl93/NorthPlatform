package pl.arieals.minigame.goldhunter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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
    
    private final SortedSet<DamagerEntry> lastDamagers = new TreeSet<>();
    
    private double totalDamage;
    
    public StatsTracker(GoldHunterPlayer player)
    {
        this.player = player;
        
        tickableManager.addTickableObject(this);
    }
    
    public GoldHunterPlayer getKiller()
    {
        return Optional.ofNullable(lastDamagers.size() > 0 ? lastDamagers.first() : null).map(entry -> entry.player).orElse(null);
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
        System.out.println(damaged.getPlayer().getName() + " damaged " + damageValue);
        
        totalDamage += damageValue;
        
        damaged.getStatsTracker().lastDamagers.add(new DamagerEntry(player));
        System.out.println(damaged.getStatsTracker().lastDamagers.size());
    }
    
    public void onDie()
    {
        incrementDeaths();
        
        GoldHunterPlayer killer = getKiller();
        if ( killer != null )
        {
            killer.getStatsTracker().incrementKills();
        }
        
        getKillerAssistants().forEach(player -> player.getStatsTracker().incrementAssists());
        
        cleanupLastDamagers();
    }
    
    private void incrementKills()
    {
        kills++;
        updateScoreboard();
    }
    
    private void incrementDeaths()
    {
        deaths++;
        updateScoreboard();
    }
    
    private void incrementAssists()
    {
        assists++;
        updateScoreboard();
    }
    
    private void updateScoreboard()
    {
        player.getScoreboardContext().set(ImmutableMap.of("kills", kills, "deaths", deaths, "assists", assists));
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
            updateScoreboard();
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
    
    @Tick
    private void cleanupLastDamagers()
    {
        int currentTick = MinecraftServer.currentTick;
        lastDamagers.removeIf(lastDamage -> lastDamage.tick - currentTick > 100);
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
            return this.tick - other.tick;
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
            return this.player.equals(other.player);
        }
        
        @Override
        public int hashCode()
        {
            return this.player.hashCode();
        }
    }
}

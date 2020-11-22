package pl.north93.northplatform.minigame.goldhunter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.DynamicBean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.minigame.goldhunter.arena.GoldHunterArena;
import pl.north93.northplatform.minigame.goldhunter.classes.CharacterClassManager;
import pl.north93.northplatform.minigame.goldhunter.classes.SpecialAbilityType;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public class GoldHunter
{
    @Inject
    private static LocalArenaManager localArenaManager;
    
    private final IBukkitServerManager serverManager;
    private final CharacterClassManager characterClassManager;

    private final Map<UUID, GoldHunterPlayer> players = new HashMap<>();
    
    @Bean
    private GoldHunter(IBukkitServerManager serverManager, CharacterClassManager characterClassManager)
    {
        this.serverManager = serverManager;
        this.characterClassManager = characterClassManager;
    }
    
    void enable()
    {
        characterClassManager.initClasses();

        runTask(() -> setWorldProperties(Bukkit.getWorlds().get(0)));
        
        registerAbilityHandlersListener();
    }
    
    void disable()
    {
        
    }
    
    public void addPlayer(GoldHunterPlayer player)
    {
        players.put(player.getPlayer().getUniqueId(), player);
    }
    
    public void removePlayer(GoldHunterPlayer player)
    {
        players.remove(player.getPlayer().getUniqueId());
    }
    
    public GoldHunterPlayer getPlayer(UUID uuid)
    {
        return players.get(uuid);
    }
    
    public GoldHunterPlayer getPlayer(Player bukkitPlayer)
    {
        return getPlayer(bukkitPlayer.getUniqueId());
    }
    
    public GoldHunterPlayer getPlayer(Entity entity)
    {
        if ( entity instanceof Player )
        {
            return getPlayer((Player) entity);
        }
        
        return null;
    }
    
    public void runTask(Runnable runnable)
    {
        Bukkit.getScheduler().runTask(serverManager.getPlugin(), runnable);
    }
    
    public BukkitTask runTask(int ticks, Runnable runnable)
    {
        return Bukkit.getScheduler().runTaskLater(serverManager.getPlugin(), runnable, ticks);
    }
    
    private void registerAbilityHandlersListener()
    {
        for ( SpecialAbilityType abilityType : SpecialAbilityType.values() )
        {
            serverManager.registerEvents(abilityType.getHandler());
        }
    }
    
    public void setWorldProperties(World world)
    {
        world.setSpawnFlags(false, false);
        world.setDifficulty(Difficulty.HARD);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setThundering(false);
        world.setStorm(false);
        
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
    }
    
    public boolean isGameWorld(World world)
    {
        return getArenaForWorld(world) != null;
    }
    
    public GoldHunterArena getArenaForWorld(World world)
    {
        LocalArena localArena = localArenaManager.getArena(world);
        
        if ( localArena == null )
        {
            return null;
        }
        
        return localArena.getArenaData();
    }
    
    @DynamicBean
    public Logger goldHunterLoggerFactory(GoldHunterLogger loggerAnnotation, @Named("Source") Class<?> source, @Named("Instance") Optional<?> instance)
    {
        if ( loggerAnnotation.useToString() )
        {
            if ( instance.isPresent() )
            {
                final Object loggerHolder = instance.get();
                return LoggerFactory.getLogger(loggerHolder.toString());
            }

            throw new IllegalStateException("GoldHunterLogger with useToString=true can be injected only into instance");
        }

        final String customName = loggerAnnotation.value();
        if ( ! customName.isEmpty() )
        {
            return LoggerFactory.getLogger(customName);
        }
        
        return LoggerFactory.getLogger(source);
    }
    
}

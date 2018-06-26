package pl.arieals.minigame.goldhunter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.goldhunter.arena.GoldHunterArena;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.entity.GoldHunterEntityUtils;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.DynamicBean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class GoldHunter
{
    @Inject
    private static MiniGameServer miniGameServer;
    
    private final BukkitApiCore apiCore;
    private final CharacterClassManager characterClassManager;
    
    private final GameHostManager gameHostManager;
    
    private final Map<UUID, GoldHunterPlayer> players = new HashMap<>();
    
    @Bean
    private GoldHunter(BukkitApiCore apiCore, CharacterClassManager characterClassManager)
    {
        this.apiCore = apiCore;
        this.characterClassManager = characterClassManager;
        this.gameHostManager = miniGameServer.getServerManager();
    }
    
    void enable()
    {
        characterClassManager.initClasses();
        GoldHunterEntityUtils.registerGoldHunterEntities();
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
        Bukkit.getScheduler().runTask(apiCore.getPluginMain(), runnable);
    }
    
    public BukkitTask runTask(int ticks, Runnable runnable)
    {
        return Bukkit.getScheduler().runTaskLater(apiCore.getPluginMain(), runnable, ticks);
    }
    
    private void registerAbilityHandlersListener()
    {
        for ( SpecialAbilityType abilityType : SpecialAbilityType.values() )
        {
            Bukkit.getPluginManager().registerEvents(abilityType.getHandler(), apiCore.getPluginMain());
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
        LocalArena localArena = gameHostManager.getArenaManager().getArena(world);
        
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
            return LogManager.getLogger(instance.get().toString());
        }
        
        if ( !loggerAnnotation.value().isEmpty() )
        {
            return LogManager.getLogger(loggerAnnotation.value());
        }
        
        return LogManager.getLogger(source);
    }
    
}

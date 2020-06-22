package pl.north93.northplatform.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.slf4j.Logger;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaStartCancelledEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaStartScheduledEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.arena.GoldHunterArena;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;

public class ArenaListener implements AutoListener
{
    private final Logger logger;
    private final GoldHunter goldHunter;
    private final ITickableManager tickableManager;
    
    public ArenaListener(@GoldHunterLogger Logger logger, GoldHunter goldHunter, ITickableManager tickableManager)
    {
        this.logger = logger;
        this.goldHunter = goldHunter;
        this.tickableManager = tickableManager;
    }
    
    @EventHandler
    public void onGameInit(GameInitEvent event)
    {
        logger.debug("Call onGameInit() with event: {}", event);
        
        LocalArena localArena = event.getArena();
        if ( localArena.getArenaData() == null )
        {
            localArena.getMetaStore().set(StandardArenaMetaData.SIGNED_PLAYERS, 0);
            localArena.setArenaData(new GoldHunterArena(localArena));
            tickableManager.addTickableObject((ITickable) localArena.getArenaData());
            logger.info("Add GoldHunter arena with uuid: {}", localArena.getId());
            //goldHunter.prepareGoldHunterArena(localArena);
        }
    }
    
    @EventHandler
    public void onLobbyInit(LobbyInitEvent event)
    {
        event.getArena().<GoldHunterArena>getArenaData().gameInit();
    }
    
    @EventHandler
    public void onGameStart(GameStartEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
        
        try
        {
            arena.gameStart();
            goldHunter.setWorldProperties(event.getArena().getWorld().getCurrentWorld());
        }
        catch ( Exception e )
        {
            logger.error("Couldn't start game for arena {} with map {}", arena, arena.getLocalArena().getWorld().getCurrentMapTemplate().getName(), e);
            arena.getPlayers().forEach(p -> p.sendMessage("game_start_fail", arena.getLocalArena().getWorld().getCurrentMapTemplate().getDisplayName()));
            arena.getLocalArena().setGamePhase(GamePhase.POST_GAME);
        }
    }
    
    @EventHandler
    public void onGameEnd(GameEndEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
        arena.gameEnd();
    }
    
    @EventHandler
    public void onPlayerJoinToArena(PlayerJoinArenaEvent event)
    {
        event.setJoinMessage(null);
        
        GoldHunterArena arena = event.getArena().getArenaData();
        GoldHunterPlayer player = new GoldHunterPlayer(event.getPlayer().getCraftPlayer(), arena);
        
        goldHunter.addPlayer(player);
        
        arena.playerJoin(player);
    }
    
    @EventHandler
    public void onPlayerLeftArena(PlayerQuitArenaEvent event)
    {
        event.setQuitMessage(null);
        
        GoldHunterArena arena = event.getArena().getArenaData();
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        arena.playerLeft(player);
        goldHunter.removePlayer(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStartScheduled(ArenaStartScheduledEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
        event.getArena().getScheduler().runTaskLater(arena::updateLobbyScoreboardLayout, 0);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onStartCancelled(ArenaStartCancelledEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
        arena.updateLobbyScoreboardLayout();
    }
}

package pl.arieals.minigame.goldhunter.listener;

import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaStartCancelledEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaStartScheduledEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.GoldHunterArena;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

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
            localArena.getMetadata().set(StandardArenaMetaData.SIGNED_PLAYERS, 0);
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
            logger.error("Couldn't start game for arena {} with map {}", arena, arena.getLocalArena().getWorld().getCurrentMapTemplate().getName());
            arena.getPlayers().forEach(p -> p.sendMessage("game_start_fail"));
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

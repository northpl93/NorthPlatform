package pl.arieals.minigame.goldhunter.listener;

import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaStartScheduledEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
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
        goldHunter.setWorldProperties(event.getArena().getWorld().getCurrentWorld());
        arena.gameStart();
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
        GoldHunterPlayer player = new GoldHunterPlayer(event.getPlayer(), arena);
        
        MiniGameApi.setPlayerData(event.getPlayer(), arena);
        MiniGameApi.setPlayerData(event.getPlayer(), player);
        
        arena.playerJoin(player);
    }
    
    @EventHandler
    public void onPlayerLeftArena(PlayerQuitArenaEvent event)
    {
        event.setQuitMessage(null);
        
        GoldHunterArena arena = event.getArena().getArenaData();
        arena.playerLeft(MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class));
    }
    
    @EventHandler
    public void onStartScheduled(ArenaStartScheduledEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
        
        if ( !arena.isEnoughSignedPlayersToStart() )
        {
            event.setCancelled(true);
        }
    }
}

package pl.arieals.minigame.goldhunter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaStartScheduledEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaListener implements Listener
{
    private final Logger logger = LogManager.getLogger("ArenaListener");
        
    @Inject
    private GoldHunterComponent goldHunter;
    @Inject
    private ITickableManager tickableManager;
    
    @EventHandler
    public void onGameInit(GameInitEvent event)
    {
        System.out.println("CHUJ");
        logger.debug("Call onGameInit() with event: {}", event);
        
        LocalArena localArena = event.getArena();
        if ( localArena.getArenaData() == null )
        {
            localArena.setArenaData(new GoldHunterArena(localArena));
            tickableManager.addTickableObject((ITickable) localArena.getArenaData());
            logger.info("Add GoldHunter arena with uuid: {}", localArena.getId());
            //goldHunter.prepareGoldHunterArena(localArena);
        }
        
        GoldHunterArena arena = localArena.getArenaData();
        arena.gameInit();
    }
    
    @EventHandler
    public void onGameStart(GameStartEvent event)
    {
        GoldHunterArena arena = event.getArena().getArenaData();
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

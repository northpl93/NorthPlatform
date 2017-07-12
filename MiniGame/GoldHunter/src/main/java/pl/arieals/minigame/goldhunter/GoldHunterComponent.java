package pl.arieals.minigame.goldhunter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.md_5.bungee.event.EventHandler;
import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class GoldHunterComponent extends Component implements Listener
{
    private final Logger logger = LogManager.getLogger("GoldHunter");
    
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private MiniGameServer minigameServer;
    @Inject
    private ITickableManager tickableManager;
    
    @Override
    protected void enableComponent()
    {
        System.out.println("##################");
        logger.info("SDdasdasasdsdasdasdsasdaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        apiCore.registerEvents(new ArenaListener());
        apiCore.registerEvents(this);
        
        System.out.println("@@@@@@ " + tickableManager);
        //IArenaData.class.getName();
    }

    @Override
    protected void disableComponent()
    {

    }
    
    public void prepareGoldHunterArena(LocalArena localArena)
    {
        
    }
    
    @EventHandler
    public void onInit(GameInitEvent event)
    {
        System.out.println("GameInit: " + event.getArena());
        logger.debug("CHUJ INIT GAME");
    }
}

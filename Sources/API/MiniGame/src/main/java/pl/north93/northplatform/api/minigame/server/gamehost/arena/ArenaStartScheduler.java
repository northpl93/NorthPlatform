package pl.north93.northplatform.api.minigame.server.gamehost.arena;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;

import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaStartCancelledEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.ArenaStartScheduledEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;

public class ArenaStartScheduler
{
    private final LocalArena arena;
    @Inject
    private GameHostManager gameHostManager;
    private SimpleCountdown startCountdown;
    
    public ArenaStartScheduler(final LocalArena arena)
    {
        this.arena = arena;
    }
    
    public SimpleCountdown getStartCountdown()
    {
        return this.startCountdown;
    }
    
    public int getGameStartCooldown()
    {
        return this.gameHostManager.getMiniGameConfig().getStartCooldown();
    }
    
    public boolean isStartScheduled()
    {
        // isStarted zwroci takze false jesli task sie zakonczyl
        return this.startCountdown != null && this.startCountdown.isStarted();
    }
    
    public void scheduleStart()
    {
        Preconditions.checkState(this.arena.getGamePhase() == GamePhase.LOBBY, "Invalid arena game phase");
        Preconditions.checkState(! this.isStartScheduled(), "Game start is already scheduled");
        
        ArenaStartScheduledEvent event = new ArenaStartScheduledEvent(this.arena, this.getGameStartCooldown());
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() )
        {
            return;
        }
        
        this.arena.startVoting();
        
        if ( event.getStartDelay() == 0 )
        {
            this.arena.startArenaGame();
            return;
        }
        
        this.startCountdown = new SimpleCountdown(event.getStartDelay()).endCallback(this.arena::startArenaGame).start();
        this.arena.getTimer().start(event.getStartDelay() / 20, TimeUnit.SECONDS, false);
    }
    
    public void cancelStarting()
    {
        Preconditions.checkState(this.startCountdown != null, "Game start isn't scheduled now");
        
        this.startCountdown.stop();
        this.startCountdown = null;
        this.arena.getTimer().stop();
        
        Bukkit.getPluginManager().callEvent(new ArenaStartCancelledEvent(arena));
    }
    
    
}

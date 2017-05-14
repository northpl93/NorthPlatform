package pl.arieals.api.minigame.server.gamehost.arena;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class ArenaStartScheduler
{
    private final LocalArena arena;
    
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer minigameServer;
    
    private int gameStartCooldown = 600;
    
    private SimpleCountdown startCountdown;
    
    public ArenaStartScheduler(LocalArena arena)
    {
        this.arena = arena;
    }
    
    public SimpleCountdown getStartCountdown()
    {
        return this.startCountdown;
    }
    
    public int getGameStartCooldown()
    {
        return this.gameStartCooldown;
    }
    
    public void setGameStartCooldown(int gameStartCooldown)
    {
        this.gameStartCooldown = gameStartCooldown;
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
        
        this.arena.startVoting();
        this.startCountdown = new SimpleCountdown(this.gameStartCooldown).endCallback(this.arena::startArenaGame).start();
    }
    
    public void cancelStarting()
    {
        Preconditions.checkState(this.startCountdown != null, "Game start isn't scheduled now");
        this.startCountdown.stop();
        this.startCountdown = null;
    }
    
    
}

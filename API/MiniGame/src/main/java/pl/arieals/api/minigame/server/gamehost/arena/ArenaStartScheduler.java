package pl.arieals.api.minigame.server.gamehost.arena;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.bukkit.utils.Countdown;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class ArenaStartScheduler
{
    private final LocalArena arena;
    
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer minigameServer;
    
    private int gameStartCooldown = 600;
    
    private Countdown startCountdown;
    
    public ArenaStartScheduler(LocalArena arena)
    {
        this.arena = arena;
    }
    
    public Countdown getStartCountdown()
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
        return this.startCountdown != null;
    }
    
    public void scheduleStart()
    {
        Preconditions.checkState(this.arena.getGamePhase() == GamePhase.LOBBY, "Invalid arena game phase");
        Preconditions.checkState(this.startCountdown == null, "Game start is alredy scheduled");
        
        this.arena.startVoting();
        this.startCountdown = new Countdown(this.gameStartCooldown).endCallback(this.arena::startArenaGame).start();
    }
    
    public void cancelStarting()
    {
        Preconditions.checkState(this.startCountdown != null, "Game start isn't scheduled now");
        this.startCountdown.stop();
        this.startCountdown = null;
    }
    
    
}

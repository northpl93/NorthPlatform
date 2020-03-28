package pl.north93.northplatform.minigame.goldhunter.event;

import org.bukkit.event.player.PlayerEvent;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public abstract class GoldHunterPlayerEvent extends PlayerEvent
{
    protected final GoldHunterPlayer player;
    
    public GoldHunterPlayerEvent(GoldHunterPlayer who)
    {
        super(who.getPlayer());
        
        this.player = who;
    }
    
    public GoldHunterPlayer getGoldHunterPlayer()
    {
        return player;
    }
}

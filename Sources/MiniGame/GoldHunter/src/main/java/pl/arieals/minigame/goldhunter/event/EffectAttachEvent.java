package pl.arieals.minigame.goldhunter.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.player.Effect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class EffectAttachEvent extends GoldHunterPlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    
    private Effect effect;
    private int duration;
    private boolean cancelled;
    
    public EffectAttachEvent(GoldHunterPlayer who, Effect effect, int duration)
    {
        super(who);
        
        this.effect = effect;
        this.duration = duration;
    }

    
    public Effect getEffect()
    {
        return effect;
    }
    
    public void setEffect(Effect effect)
    {
        this.effect = effect;
    }
    
    public Class<? extends Effect> getEffectType()
    {
        return effect.getClass();
    }
    
    public int getDuration()
    {
        return duration;
    }
    
    public void setDuration(int duration)
    {
        Preconditions.checkArgument(duration >= -1, "Effect duration must be greater or equal -1");
        this.duration = duration;
    }
    
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag)
    {
        cancelled = flag;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}

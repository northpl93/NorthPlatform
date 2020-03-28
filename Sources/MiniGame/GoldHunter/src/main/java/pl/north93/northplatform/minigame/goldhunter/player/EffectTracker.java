package pl.north93.northplatform.minigame.goldhunter.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

import lombok.NonNull;

import pl.north93.northplatform.minigame.goldhunter.event.EffectAttachEvent;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class EffectTracker
{
    @Inject
    private static ITickableManager tickableManager;
    
    private final Map<Class<? extends Effect>, Effect> activeEffects = new HashMap<>();
    
    public final GoldHunterPlayer player;
    
    public EffectTracker(GoldHunterPlayer player)
    {
        this.player = player;
    }
    
    public GoldHunterPlayer getPlayer()
    {
        return player;
    }
    
    public void addEffect(Effect effect)
    {
        addEffect(effect, -1);
    }
    
    public void addEffect(@NonNull Effect effect, int duration)
    {
        Preconditions.checkArgument(duration >= -1);
        
        EffectAttachEvent event = new EffectAttachEvent(player, effect, duration);
        Bukkit.getPluginManager().callEvent(event);
        
        if ( event.isCancelled() || event.getEffect() == null )
        {
            return;
        }
        
        effect = event.getEffect();
        duration = event.getDuration();
        
        Effect currentEffect = getEffect(effect.getClass());
        if ( effect.equals(currentEffect) ) // effect is the same so we set to current effect greater duration
        {
            currentEffect.setDuration(currentEffect.isInfinite() || duration == -1 ? -1 : Math.max(duration, currentEffect.getDuration()));
        }
        else if ( currentEffect == null || effect.compareTo(currentEffect) >= 0 ) // new effect is better than current one so we add new effect
        {
            addEffect0(effect, duration);
        }
    }

    private void addEffect0(Effect effect, int duration)
    {
        removeEffect(effect.getClass());
        
        effect.attach(this, duration);
        tickableManager.addTickableObject(effect);
        activeEffects.put(effect.getClass(), effect);
    }
    
    public boolean removeEffect(Class<? extends Effect> effectType)
    {
        Effect effect = activeEffects.remove(effectType);
        if ( effect != null )
        {
            effect.detach();
            tickableManager.removeTickableObject(effect);
            return true;
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Effect> T getEffect(Class<T> effectType)
    {
        return (T) activeEffects.get(effectType);
    }
    
    public boolean hasEffectOfType(Class<? extends Effect> effectType)
    {
        return getEffect(effectType) != null;
    }
    
    public Collection<Effect> getActiveEffects()
    {
        return new HashSet<>(activeEffects.values());
    }
    
    public void clearEffects()
    {
        getActiveEffects().forEach(e -> removeEffect(e.getClass()));
    }
    
    void notifyEffectEnd(Effect effect)
    {
        Preconditions.checkState(removeEffect(effect.getClass()));
    }
}

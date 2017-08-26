package pl.arieals.minigame.goldhunter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.utils.ISyncCallback;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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
    
    public ISyncCallback addEffect(Effect effect)
    {
        return addEffect(effect, -1);
    }
    
    public ISyncCallback addEffect(Effect effect, int duration)
    {
        Preconditions.checkNotNull(effect);
        Preconditions.checkArgument(duration >= -1);
        
        removeEffect(effect.getClass());
        
        effect.attach(this, duration);
        tickableManager.addTickableObject(effect);
        activeEffects.put(effect.getClass(), effect);
        
        return effect.getCallback();
    }
    
    public boolean removeEffect(Class<? extends Effect> effectType)
    {
        Effect effect = activeEffects.remove(effectType);
        if ( effect != null )
        {
            effect.detach();
            tickableManager.removeTickableObject(effect);
            System.out.println("Removed tickable object");
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

package pl.north93.zgame.api.bukkit.tick;

import java.util.Collection;

public interface ITickableManager
{
    void addTickableObjects(Collection<? extends ITickable> objects);
    
    void removeTickableObjects(Collection<? extends ITickable> objects);
}

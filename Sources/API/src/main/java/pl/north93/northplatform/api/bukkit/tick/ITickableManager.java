package pl.north93.northplatform.api.bukkit.tick;

import java.util.Collection;

public interface ITickableManager
{
    void addTickableObject(ITickable tickable);
    
    void removeTickableObject(ITickable tickable);
    
    void addTickableObjectsCollection(Collection<? extends ITickable> objects);
    
    void removeTickableObjectsCollection(Collection<? extends ITickable> objects);
}

package pl.north93.zgame.api.bukkit.tick.impl;

import java.lang.ref.WeakReference;

import pl.north93.zgame.api.bukkit.tick.ITickable;

class TickableWeakReference extends WeakReference<ITickable>
{
    private final int identityHash;
    
    public TickableWeakReference(ITickable referent)
    {
        super(referent);
        this.identityHash = System.identityHashCode(referent);
    }
    
    @Override
    public int hashCode()
    {
        return identityHash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if ( !( obj instanceof TickableWeakReference ) )
        {
            return false;
        }
        
        TickableWeakReference other = (TickableWeakReference) obj;
        
        ITickable ref1 = other.get();
        ITickable ref2 = other.get();
        if ( ref1 == null || ref2 == null )
        {
            return false;
        }
        
        return ref1 == ref2;
    }
}
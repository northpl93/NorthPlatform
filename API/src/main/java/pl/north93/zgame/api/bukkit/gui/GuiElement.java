package pl.north93.zgame.api.bukkit.gui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

public abstract class GuiElement
{
    private WeakReference<Gui> gui = new WeakReference<>(null);
    private final List<String> onClickHandlers = new ArrayList<>();
    
    final void onElementAdd(Gui gui)
    {
        Preconditions.checkArgument(gui != null);
        Preconditions.checkState(this.gui.get() == null, "Cannot add this element: this element is attached to other gui.");
        this.gui = new WeakReference<>(gui);
    }
    
    final void onElementRemove(Gui gui)
    {
        Preconditions.checkState(this.gui.get() == gui);
        this.gui.clear();
    }
    
    final void markDirty()
    {
        Gui gui = this.gui.get();
        if ( gui != null )
        {
            gui.markDirty();
        }
    }
    
    public abstract void render(GuiContent content);
    
    public List<String> getOnClickHandlers()
    {
        return onClickHandlers;
    }
    
    public final void onClick(Player player)
    {
        for ( String onClickHandler : onClickHandlers )
        {
            
            // TODO: call click event
        }
    }
    
    @Override
    public final boolean equals(Object obj)
    {
        return super.equals(obj);
    }
    
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }
}

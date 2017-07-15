package pl.north93.zgame.api.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

public class Gui
{
    private final List<GuiTrackerEntry> viewers = new ArrayList<>();
    private final List<GuiElement> elements = new ArrayList<>();
    
    public final void open(Player player)
    {
        // TODO
    }
    
    public final boolean close(Player player)
    {
        return false;
        // TODO:
    }
    
    public final void closeAll()
    {
        viewers.stream().map(viewer -> viewer.getPlayer()).forEach(player -> close(player));
    }
    
    public final Collection<Player> getViewers()
    {
        return viewers.stream().map(viewer -> viewer.getPlayer()).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public final List<GuiElement> getElements()
    {
        return elements;
    }
    
    @SuppressWarnings("unchecked")
    public final <T extends GuiElement> List<T> getElementsByClass(Class<T> clazz)
    {
        return (List<T>) elements.stream().filter(element -> clazz.isInstance(element))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    // TODO: get element in slot
    
    public final void addElement(GuiElement element)
    {
        element.onElementAdd(this);
        elements.add(element);
        markDirty();
    }
    
    public final void removeElement(GuiElement element)
    {
        if ( elements.remove(element) )
        {
            element.onElementRemove(this);
        }
    }
    
    public final void clearElements()
    {
        Iterator<GuiElement> it = elements.iterator();
        while ( it.hasNext() )
        {
            GuiElement element = it.next();
            it.remove();
            element.onElementRemove(this);
        }
    }
    
    public final void markDirty()
    {
        
    }

    protected void onOpen(Player player)
    {
    }
    
    protected void onClose(Player player)
    {
    }
    
    @ClickHandler
    public void closeInventory(ClickEvent event)
    {
        close(event.getWhoClicked());
    }
}

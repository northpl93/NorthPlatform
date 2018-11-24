package pl.north93.northplatform.api.bukkit.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickHandler;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickable;

public class HotbarEntry implements IClickable
{
    private final HotbarMenu hotbarMenu;
    private final int slot;
    
    private final List<IClickHandler> clickHandlers = new ArrayList<>();
    private final Map<String, String> metadata      = new HashMap<>();
    
    private IGuiIcon icon;
    
    private boolean visible = true;
    
    public HotbarEntry(HotbarMenu hotbarMenu, int slot)
    {
        this.slot = slot;
        this.hotbarMenu = hotbarMenu;
    }
    
    public HotbarMenu getHotbarMenu()
    {
        return hotbarMenu;
    }
    
    public int getSlot()
    {
        return slot;
    }
    
    public List<IClickHandler> getClickHandlers()
    {
        return clickHandlers;
    }
    
    public Map<String, String> getMetadata()
    {
        return metadata;
    }
    
    public IGuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(IGuiIcon icon)
    {
        this.icon = icon;
        markDirty();
    }
    
    public boolean isDirty()
    {
        return hotbarMenu.isDirty();
    }
    
    public void markDirty()
    {
        hotbarMenu.markDirty();
    }
    
    public void hide()
    {
        setVisible(false);
    }
    
    public void show()
    {
        setVisible(true);
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        markDirty();
    }
    
    public boolean isVisible()
    {
        return visible;
    }
}

package pl.north93.zgame.api.bukkit.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.north93.zgame.api.bukkit.gui.impl.IClickable;

public class HotbarEntry implements IClickable
{
    private final HotbarMenu hotbarMenu;
    private final int slot;
    
    private final List<String> clickHandlers = new ArrayList<>();
    private final Map<String, String> metadata = new HashMap<>();
    
    private GuiIcon icon;
    
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
    
    public List<String> getClickHandlers()
    {
        return clickHandlers;
    }
    
    public Map<String, String> getMetadata()
    {
        return metadata;
    }
    
    public GuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(GuiIcon icon)
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
}

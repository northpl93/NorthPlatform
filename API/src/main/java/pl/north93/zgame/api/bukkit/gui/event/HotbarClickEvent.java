package pl.north93.zgame.api.bukkit.gui.event;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.ClickType;
import pl.north93.zgame.api.bukkit.gui.HotbarEntry;

public class HotbarClickEvent extends ClickEvent
{
    private final HotbarEntry clickedEntry;
    
    public HotbarClickEvent(Player player, ClickType type, HotbarEntry clickedEntry)
    {
        super(player, type);
        this.clickedEntry = clickedEntry;
    }
    
    public HotbarEntry getClickedEntry()
    {
        return clickedEntry;
    }
}

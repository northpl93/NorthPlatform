package pl.north93.zgame.api.bukkit.gui.event;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.ClickType;

public class ClickEvent
{
    private final Player whoClicked;
    private final ClickType clickType;
    
    public ClickEvent(Player whoClicked, ClickType clickType)
    {
        this.whoClicked = whoClicked;
        this.clickType = clickType;
    }
    
    public Player getWhoClicked()
    {
        return whoClicked;
    }
    
    public ClickType getClickType()
    {
        return clickType;
    }
}

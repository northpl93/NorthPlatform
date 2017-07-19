package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;

public class ClickEvent
{
    private final Player whoClicked;
    private final ClickType clickType;
    
    public ClickEvent(Player player, ClickType type)
    {
        this.whoClicked = player;
        this.clickType = type;
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

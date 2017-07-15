package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;

public class ClickEvent
{
    private final Player whoClicked;
    
    public ClickEvent(Player player)
    {
        this.whoClicked = player;
    }
    
    public Player getWhoClicked()
    {
        return whoClicked;
    }
}

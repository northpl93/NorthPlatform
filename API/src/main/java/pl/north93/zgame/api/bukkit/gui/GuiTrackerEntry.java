package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;

public class GuiTrackerEntry
{
    private final Player player;
    
    public GuiTrackerEntry(Player player)
    {
        this.player = player;
    }
    
    public Player getPlayer()
    {
        return player;
    }
}

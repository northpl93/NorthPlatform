package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.impl.ClickEvent;

public class GuiClickEvent extends ClickEvent
{
    private final GuiElement clickedElement;
    
    public GuiClickEvent(Player player, ClickType type, GuiElement clickedElement)
    {
        super(player, type);
        this.clickedElement = clickedElement;
    }
    
    public GuiElement getClickedElement()
    {
        return clickedElement;
    }
}

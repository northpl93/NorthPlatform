package pl.north93.northplatform.api.bukkit.gui.event;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.element.GuiElement;
import pl.north93.northplatform.api.bukkit.gui.ClickType;

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

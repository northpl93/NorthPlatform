package pl.north93.zgame.api.bukkit.gui.impl;

import pl.north93.zgame.api.bukkit.gui.ClickEvent;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;

public class TestGui extends Gui
{
    public TestGui()
    {
        super("example");
    }
    
    @ClickHandler("Accept")
    public void accept(ClickEvent event)
    {
        event.getWhoClicked().sendMessage("Clicked");
    }
}

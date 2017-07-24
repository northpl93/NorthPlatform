package pl.north93.zgame.api.bukkit.gui.impl;

import pl.north93.zgame.api.bukkit.gui.GuiClickEvent;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class TestGui extends Gui
{
    private static MessagesBox messagesBox = new MessagesBox(TestGui.class.getClassLoader(), "Messages");

    public TestGui()
    {
        super(messagesBox, "example");
    }
    
    @ClickHandler("Accept")
    public void accept(GuiClickEvent event)
    {
        event.getWhoClicked().sendMessage("Clicked");
    }
}

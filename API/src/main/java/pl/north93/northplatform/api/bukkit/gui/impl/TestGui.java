package pl.north93.northplatform.api.bukkit.gui.impl;

import pl.north93.northplatform.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.Gui;

public class TestGui extends Gui
{
    private static MessagesBox messagesBox = new MessagesBox(TestGui.class.getClassLoader(), "Messages");

    public TestGui()
    {
        super(messagesBox, "example");
    }
    
    @ClickHandler
    public void accept(GuiClickEvent event)
    {
        event.getWhoClicked().sendMessage("Clicked");
    }
}

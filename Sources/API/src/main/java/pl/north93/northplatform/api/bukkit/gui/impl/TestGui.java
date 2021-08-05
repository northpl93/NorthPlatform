package pl.north93.northplatform.api.bukkit.gui.impl;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
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

    public static class OpenTestGuiCmd extends NorthCommand
    {

        public OpenTestGuiCmd()
        {
            super("opentestgui");
        }

        @Override
        public void execute(final NorthCommandSender sender, final Arguments args, final String label)
        {
            new TestGui().open(((Player) sender.unwrapped()));
        }
    }
}

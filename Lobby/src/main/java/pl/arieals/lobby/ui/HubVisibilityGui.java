package pl.arieals.lobby.ui;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class HubVisibilityGui extends Gui
{
    @Inject @Messages("UserInterface")
    private static MessagesBox messages;

    public HubVisibilityGui()
    {
        super(messages, "hub/visibility");
    }
}

package pl.north93.northplatform.lobby.ui;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class HubInstanceGui extends Gui
{
    @Inject @Messages("UserInterface")
    private static MessagesBox messages;

    public HubInstanceGui()
    {
        super(messages, "hub/instance_picker");
    }
}

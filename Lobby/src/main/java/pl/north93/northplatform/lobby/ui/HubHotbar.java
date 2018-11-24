package pl.north93.northplatform.lobby.ui;

import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.HotbarMenu;
import pl.north93.northplatform.api.bukkit.gui.event.HotbarClickEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class HubHotbar extends HotbarMenu
{
    @Inject @Messages("UserInterface")
    private static MessagesBox messages;

    public HubHotbar()
    {
        super(messages, "hub");
    }

    @ClickHandler
    public void openGamePicker(final HotbarClickEvent event)
    {
        this.getViewers().forEach(viewer -> new HubPickGui().open(viewer));
    }

    @ClickHandler
    public void changeVisibility(final HotbarClickEvent event)
    {
        this.getViewers().forEach(viewer -> new HubVisibilityGui().open(viewer));
    }

    @ClickHandler
    public void openHubInstancePicker(final HotbarClickEvent event)
    {
        this.getViewers().forEach(viewer -> new HubInstanceGui().open(viewer));
    }
}

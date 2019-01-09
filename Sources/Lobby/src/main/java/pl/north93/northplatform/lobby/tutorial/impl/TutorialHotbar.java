package pl.north93.northplatform.lobby.tutorial.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.lobby.tutorial.ITutorialManager;
import pl.north93.northplatform.api.bukkit.gui.ClickHandler;
import pl.north93.northplatform.api.bukkit.gui.HotbarMenu;
import pl.north93.northplatform.api.bukkit.gui.event.HotbarClickEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class TutorialHotbar extends HotbarMenu
{
    @Inject @Messages("UserInterface")
    private static MessagesBox messages;
    @Inject
    private ITutorialManager tutorialManager;

    public TutorialHotbar()
    {
        super(messages, "tutorial");
    }

    @ClickHandler
    public void exitTutorial(final HotbarClickEvent event)
    {
        this.tutorialManager.exitTutorial(event.getWhoClicked());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

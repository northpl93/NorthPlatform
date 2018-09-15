package pl.arieals.lobby.ui;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class HubPickGui extends Gui
{
    @Inject @Messages("UserInterface")
    private static MessagesBox messages;

    public HubPickGui()
    {
        super(messages, "hub/game_picker");
    }

    @ClickHandler
    public void openWebShop(final GuiClickEvent event) // wywolane przez onClick w game_picker.xml
    {
        final Player player = event.getWhoClicked();
        this.close(player);

        final BaseComponent message = messages.getComponent(player.getLocale(), "gamegui.itemshop.click");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcpiraci.pl/shop"));

        player.sendMessage(message);
    }
}

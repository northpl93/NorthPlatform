package pl.arieals.lobby.gui.elytrarace;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ElytraShopMain extends Gui
{
    @Inject @Messages("ShopElytraRace")
    private static MessagesBox messagesBox;

    public ElytraShopMain()
    {
        super(messagesBox, "elytrarace/elytrarace_shop_main");
    }
}

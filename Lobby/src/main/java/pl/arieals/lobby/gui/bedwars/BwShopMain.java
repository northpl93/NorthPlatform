package pl.arieals.lobby.gui.bedwars;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BwShopMain extends Gui
{
    @Inject @Messages("")
    private static MessagesBox messagesBox;

    protected BwShopMain()
    {
        super(messagesBox, "bedwars/bw_shop_main");
    }
}

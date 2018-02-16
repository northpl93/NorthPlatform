package pl.arieals.lobby.gui.elytrarace;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

public class ElytraShopMain extends Gui
{
    @Inject @Messages("ShopElytraRace")
    private static MessagesBox messagesBox;

    public ElytraShopMain(final Player player)
    {
        super(messagesBox, "elytrarace/elytrarace_shop_main");
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

package pl.arieals.lobby.gui.bedwars;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

public class BwShopElimination extends Gui
{
    @Inject @Messages("ShopBedWars")
    private static MessagesBox messagesBox;

    public BwShopElimination(final Player player)
    {
        super(messagesBox, "bedwars/bw_shop_elimination");
        this.addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

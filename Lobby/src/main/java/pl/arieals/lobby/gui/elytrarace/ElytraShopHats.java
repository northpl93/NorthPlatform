package pl.arieals.lobby.gui.elytrarace;

import org.bukkit.entity.Player;

import pl.arieals.lobby.gui.ShopGui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

public class ElytraShopHats extends ShopGui
{
    @Inject @Messages("ShopElytraRace")
    private static MessagesBox messagesBox;

    public ElytraShopHats(final Player player)
    {
        super(messagesBox, "elytrarace/elytrarace_shop_hats", player, "elytra_hats");
        this.addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

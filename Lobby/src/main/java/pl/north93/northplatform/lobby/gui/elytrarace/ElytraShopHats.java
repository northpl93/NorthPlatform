package pl.north93.northplatform.lobby.gui.elytrarace;

import org.bukkit.entity.Player;

import pl.north93.northplatform.lobby.gui.ShopGui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

public class ElytraShopHats extends ShopGui
{
    @Inject @Messages("ShopElytraRace")
    private static MessagesBox messagesBox;

    public ElytraShopHats(final Player player)
    {
        super(messagesBox, "elytrarace/elytrarace_shop_hats", player, "elytra_hats");
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

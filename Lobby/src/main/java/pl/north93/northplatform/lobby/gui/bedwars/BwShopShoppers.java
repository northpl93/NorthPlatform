package pl.north93.northplatform.lobby.gui.bedwars;

import org.bukkit.entity.Player;

import pl.north93.northplatform.lobby.gui.ShopGui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

public class BwShopShoppers extends ShopGui
{
    @Inject @Messages("ShopBedWars")
    private static MessagesBox messagesBox;

    public BwShopShoppers(final Player player)
    {
        super(messagesBox, "bedwars/bw_shop_shoppers", player, "bedwars_shoppers");
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

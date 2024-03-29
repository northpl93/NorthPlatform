package pl.north93.northplatform.lobby.gui.elytrarace;

import org.bukkit.entity.Player;

import pl.north93.northplatform.lobby.gui.ShopGui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

public class ElytraShopEffects extends ShopGui
{
    @Inject @Messages("ShopElytraRace")
    private static MessagesBox messagesBox;

    public ElytraShopEffects(final Player player)
    {
        super(messagesBox, "elytrarace/elytrarace_shop_effects", player, "elytra_effects");
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

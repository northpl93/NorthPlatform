package pl.north93.northplatform.minigame.bedwars.shop.gui;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

public abstract class ShopBase extends Gui
{
    @Inject @Messages("BedWarsShop")
    protected static MessagesBox shopMessages;

    protected ShopBase(final Player player, final String layout)
    {
        super(shopMessages, layout);
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

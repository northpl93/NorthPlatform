package pl.arieals.minigame.bedwars.shop.gui;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

public abstract class ShopBase extends Gui
{
    @Inject @Messages("BedWarsShop")
    protected static MessagesBox shopMessages;

    protected ShopBase(final Player player, final String layout)
    {
        super(shopMessages, layout);
        this.addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

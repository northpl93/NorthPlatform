package pl.arieals.minigame.bedwars.shop.gui;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

public class UpgradesGui extends Gui
{
    @Inject @Messages("BedWarsShop")
    private static MessagesBox messageBox;

    public UpgradesGui(final Player player)
    {
        super(messageBox, "upgrades");
        this.getContent().addVariables(Vars.of("$playerId", player.getUniqueId()));
    }
}

package pl.arieals.minigame.bedwars.shop.gui;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

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

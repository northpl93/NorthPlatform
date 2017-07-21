package pl.arieals.minigame.bedwars.shop.gui;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class UpgradesGui extends Gui
{
    @Inject @Messages("BedWarsShop")
    private static MessagesBox messageBox;

    public UpgradesGui()
    {
        super(messageBox, "upgrades");
    }
}

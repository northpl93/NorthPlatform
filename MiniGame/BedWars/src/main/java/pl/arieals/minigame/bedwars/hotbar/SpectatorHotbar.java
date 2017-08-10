package pl.arieals.minigame.bedwars.hotbar;

import pl.north93.zgame.api.bukkit.gui.HotbarMenu;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class SpectatorHotbar extends HotbarMenu
{
    @Inject @Messages("BedWars")
    private static MessagesBox messages;

    public SpectatorHotbar()
    {
        super(messages, "spectator");
    }
}

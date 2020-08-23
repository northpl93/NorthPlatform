package pl.north93.northplatform.api.minigame.server.gamehost.cmd;

import java.util.Collections;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;

public class HubCmd extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private GameHostManager gameHostManager;

    public HubCmd()
    {
        super("hub", "lobby", "wyjdz", "wroc");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        final String myHubId = this.gameHostManager.getMiniGameConfig().getHubId();
        this.gameHostManager.tpToHub(Collections.singleton(player), myHubId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.northplatform.api.minigame.server.lobby.hub.cmd;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;

public class HubsCmd extends NorthCommand
{
    @Inject
    private LobbyManager lobbyManager;

    public HubsCmd()
    {
        super("hubs");
        this.setPermission("minigameapi.cmd.hubs");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        if (args.length() == 1)
        {
            final String hubName = args.asString(0);
            this.lobbyManager.tpToHub(Collections.singleton(player), hubName);
        }
        else
        {
            final Collection<HubWorld> localWorlds = this.lobbyManager.getLocalHub().getLocalWorlds();
            final String message = localWorlds.stream().map(HubWorld::getHubId).collect(Collectors.joining(","));
            player.sendMessage(ChatColor.RED + "Lista hubów: " + message);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

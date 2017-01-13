package pl.north93.zgame.skyblock.server.cmd.admin;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class WtfMyIsland extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    public WtfMyIsland()
    {
        super("wtfmyisland");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getOnlinePlayer(sender.getName()));
        if (skyPlayer.hasIsland())
        {
            final IslandData island = this.server.getIslandDao().getIsland(skyPlayer.getIslandId());
            sender.sendMessage("&aUUID: " + island.getIslandId());
            sender.sendMessage("&aServer: " + island.getServerId());
            sender.sendMessage("&aType: " + island.getIslandType());
            sender.sendMessage("&aName: " + island.getName());
            sender.sendMessage("&aInvites: " + island.getInvitations());
            sender.sendMessage("&aMembers: " + island.getMembersUuid());
        }
        else
        {
            sender.sendMessage("&cNie masz wyspy.");
        }
    }
}

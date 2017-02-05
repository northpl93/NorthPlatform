package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class InviteCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages;

    public InviteCmd()
    {
        super("invite", "zapros", "dodaj");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.invite.args");
            return;
        }

        final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getOnlinePlayer(sender.getName()));
        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }
        if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
        {
            sender.sendMessage(this.messages, "error.you_must_be_owner");
            return;
        }

        this.server.getSkyBlockManager().invitePlayer(skyPlayer.getIslandId(), args.asString(0));
    }
}

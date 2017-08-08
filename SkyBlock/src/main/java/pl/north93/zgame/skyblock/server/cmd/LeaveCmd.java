package pl.north93.zgame.skyblock.server.cmd;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class LeaveCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

    public LeaveCmd()
    {
        super("leave", "opusc");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Value<IOnlinePlayer> onlineSender = this.networkManager.getPlayers().unsafe().getOnline(sender.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(onlineSender);

        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }

        if (skyPlayer.getIslandRole().equals(IslandRole.OWNER))
        {
            sender.sendMessage(this.messages, "error.you_must_be_member");
            return;
        }

        this.server.getSkyBlockManager().leaveIsland(skyPlayer.getIslandId(), sender.getName(), sender.getName(), true);
    }
}

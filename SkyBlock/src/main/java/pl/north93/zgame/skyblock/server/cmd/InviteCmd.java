package pl.north93.zgame.skyblock.server.cmd;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class InviteCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

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

        final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getPlayers().unsafe().getOnline(sender.getName()));
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

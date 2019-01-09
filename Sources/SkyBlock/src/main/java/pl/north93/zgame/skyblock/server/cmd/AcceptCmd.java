package pl.north93.zgame.skyblock.server.cmd;

import java.util.UUID;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class AcceptCmd extends NorthCommand
{
    private ApiCore         apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

    public AcceptCmd()
    {
        super("accept", "akceptuj", "zaakceptuj");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.accept.args");
            return;
        }

        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final UUID islandId = this.islandIdFromOwnerName(args.asString(0));
            if (islandId != null)
            {
                this.server.getSkyBlockManager().invitationAccepted(islandId, sender.getName());
            }
            else
            {
                sender.sendMessage(this.messages, "cmd.accept.no_invite");
            }
        });
    }

    private UUID islandIdFromOwnerName(final String owner)
    {
        return this.networkManager.getPlayers().getUuidFromNick(owner);
    }
}

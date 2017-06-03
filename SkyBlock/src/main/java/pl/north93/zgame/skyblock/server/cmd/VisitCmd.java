package pl.north93.zgame.skyblock.server.cmd;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class VisitCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

    public VisitCmd()
    {
        super("visit", "odwiedz", "odw");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.visit.args");
            return;
        }

        final String visited = args.asString(0);
        final UUID islandId = this.islandIdFromOwnerName(visited);
        if (islandId == null)
        {
            sender.sendMessage(this.messages, "cmd.visit.no_player");
            return;
        }
        this.server.getSkyBlockManager().visitIsland(islandId, sender.getName());
    }

    private UUID islandIdFromOwnerName(final String owner)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(owner))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            return skyPlayer.getIslandId();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

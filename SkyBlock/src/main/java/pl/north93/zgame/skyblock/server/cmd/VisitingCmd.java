package pl.north93.zgame.skyblock.server.cmd;

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
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class VisitingCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectMessages("SkyBlock")
    private MessagesBox     messages;

    public VisitingCmd()
    {
        super("visiting", "odwiedziny", "odwiedzanie");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(sender.getName()))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
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

            this.server.getIslandDao().modifyIsland(skyPlayer.getIslandId(), islandData ->
            {
                final Boolean newState = ! islandData.getAcceptingVisits();
                sender.sendMessage(this.messages, newState ? "cmd.visiting.enabled" : "cmd.visiting.disabled");
                islandData.setAcceptingVisits(newState);
            });
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

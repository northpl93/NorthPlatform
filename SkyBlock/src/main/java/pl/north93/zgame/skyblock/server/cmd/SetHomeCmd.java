package pl.north93.zgame.skyblock.server.cmd;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.shared.api.ServerMode;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class SetHomeCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectMessages("SkyBlock")
    private MessagesBox     messages;

    public SetHomeCmd()
    {
        super("sethome", "ustawdom", "zmiendom");
        this.setAsync(true); // async because we're doing database update, getIsland query
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

            if (this.server.getServerMode().equals(ServerMode.LOBBY))
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            final Island island = this.server.<IslandHostManager>getServerManager().getIsland(skyPlayer.getIslandId());
            if (island == null)
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            final Player player = (Player) sender.unwrapped();
            final Location newLocation = player.getLocation();

            if (! island.getLocation().isInside(newLocation))
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            island.setHomeLocation(newLocation);
            sender.sendMessage(this.messages, "info.home_changed");
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

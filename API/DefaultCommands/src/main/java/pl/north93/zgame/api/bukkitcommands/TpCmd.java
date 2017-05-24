package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.actions.TeleportToPlayer;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.server.Server;

public class TpCmd extends NorthCommand
{
    private ApiCore         apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public TpCmd()
    {
        super("tp");
        this.setPermission("api.command.tp");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() > 2 || args.length() < 1)
        {
            sender.sendMessage("&c/tp [nick] <nick>");
            return;
        }

        final int destinationArg;
        final String origin;
        if(args.length() == 2)
        {
            //players teleports to a plyer
            destinationArg = 1;
            origin = args.asString(0);
        }
        else
        {
            //command sender teleports to a player
            destinationArg = 0;
            origin = sender.getName();
        }


        final String destination = args.asString( destinationArg );
        final Player destinationBukkitPlayer = Bukkit.getPlayer(destination);
        final Player originBukkitPlayer = Bukkit.getPlayer(origin);
        if (destinationBukkitPlayer != null && originBukkitPlayer != null)
        {
            originBukkitPlayer.teleport(destinationBukkitPlayer.getLocation());
            return;
        }

        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final IOnlinePlayer playerSender = this.networkManager.getOnlinePlayer(origin).get();
            final IOnlinePlayer player = this.networkManager.getOnlinePlayer(destination).get();
            if (player == null || playerSender == null)
            {
                sender.sendMessage("&cGracz jest offline");
                return;
            }

            final Server destinationServer = this.networkManager.getServers().withUuid(player.getServerId());
            playerSender.connectTo(destinationServer, new TeleportToPlayer(player.getUuid()));
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.northplatform.features.bukkit.tp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;

public class TpCommand extends NorthCommand
{
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private IPlayersManager playersManager;
    @Inject
    private IServersManager serversManager;

    public TpCommand()
    {
        super("tp");
        this.setPermission("basefeatures.cmd.tp");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() > 2 || args.length() < 1)
        {
            sender.sendMessage("&c/tp [kto] <do kogo>");
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
            originBukkitPlayer.teleport(destinationBukkitPlayer.getLocation(), TeleportCause.COMMAND);
            return;
        }

        this.bukkitExecutor.async(() ->
        {
            final IOnlinePlayer playerSender = this.playersManager.unsafe().getOnlineValue(origin).get();
            final IOnlinePlayer player = this.playersManager.unsafe().getOnlineValue(destination).get();
            if (player == null || playerSender == null)
            {
                sender.sendMessage("&cGracz jest offline");
                return;
            }

            final Server destinationServer = this.serversManager.withUuid(player.getServerId());
            playerSender.connectTo(destinationServer, new TeleportToPlayer(player.getUuid(), true));
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

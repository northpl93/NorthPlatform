package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkPlayer;

public class PlayerInfoCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (!sender.hasPermission("api.command.playerinfo") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        if (args.length == 1)
        {
            API.getPlatformConnector().runTaskAsynchronously(() ->
            {
                final NetworkPlayer networkPlayer = API.getApiCore().getNetworkManager().getNetworkPlayer(args[0]);
                if (networkPlayer == null)
                {
                    sender.sendMessage(getBukkitMessage("command.no_player"));
                    return;
                }
                sender.sendMessage("Nick: " + networkPlayer.getNick());
                sender.sendMessage("UUID: " + networkPlayer.getUuid());
                sender.sendMessage("Połączenie: " + networkPlayer.getProxyId() + " <-> " + networkPlayer.getServer());
                sender.sendMessage("Ranga: " + networkPlayer.getGroup().getName());
            });
        }
        else
        {
            sender.sendMessage(getBukkitMessage("command.usage", s, "<nick gracza>"));
        }

        return true;
    }
}

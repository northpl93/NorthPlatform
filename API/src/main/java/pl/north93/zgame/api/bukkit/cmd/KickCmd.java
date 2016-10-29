package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkPlayer;

public class KickCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (!sender.hasPermission("api.command.kick") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        if (args.length < 2)
        {
            sender.sendMessage(getBukkitMessage("command.usage", s, "<nick gracza> <wiadomosc wyrzucenia>"));
            return true;
        }

        API.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final NetworkPlayer networkPlayer = API.getApiCore().getNetworkManager().getNetworkPlayer(args[0]);
            if (networkPlayer == null)
            {
                sender.sendMessage(getBukkitMessage("command.no_player"));
                return;
            }

            final String reason = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), ' ');
            final String kickMessage;
            if (StringUtils.isEmpty(reason))
            {
                kickMessage = getBukkitMessage("kick.by_command.without_reason");
            }
            else
            {
                kickMessage = getBukkitMessage("kick.by_command.with_reason", reason);
            }

            networkPlayer.kick(kickMessage);
        });

        return true;
    }
}

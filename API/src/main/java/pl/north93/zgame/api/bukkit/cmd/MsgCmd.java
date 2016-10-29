package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkPlayer;

public class MsgCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (!sender.hasPermission("api.command.msg") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        if (args.length < 2)
        {
            sender.sendMessage(getBukkitMessage("command.usage", s, "<nick gracza> <wiadomosc>"));
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

            final String message = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), ' ');
            sender.sendMessage(getBukkitMessage("command.msg.message", getBukkitMessage("command.msg.you"), networkPlayer.getNick(), message));
            networkPlayer.sendMessage(getBukkitMessage("command.msg.message", sender.getName(), "ty", message));
        });

        return true;
    }
}

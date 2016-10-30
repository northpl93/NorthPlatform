package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;

public class NetworkControllerPing implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (! sender.hasPermission("api.command.networkcontrollerping") && ! sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Testowanie połączenia z kontrolerem sieci");
        try
        {
            final NetworkControllerRpc controllerRpc = API.getNetworkManager().getNetworkController();
            final long start = System.nanoTime();
            controllerRpc.ping();
            final long stop = System.nanoTime() - start;
            sender.sendMessage("Kontroler sieci odpowiedzial w " + stop + "ns (" + TimeUnit.NANOSECONDS.toMillis(stop) + "ms)");
        }
        catch (final UndeclaredThrowableException e)
        {
            sender.sendMessage(ChatColor.RED + "Problem podczas testowania: " + e.getCause());
        }

        return true;
    }
}

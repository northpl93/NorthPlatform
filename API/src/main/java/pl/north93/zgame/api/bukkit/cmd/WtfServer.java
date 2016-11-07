package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.server.Server;

public class WtfServer implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (!sender.hasPermission("api.command.wtfserver") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        final BukkitApiCore api = (BukkitApiCore) API.getApiCore();

        final Server server = api.getServer();
        sender.sendMessage("ID serwera: " + api.getId());
        sender.sendMessage("Nazwa w proxy: " + server.getProxyName());
        sender.sendMessage("Typ serwera: " + server.getType());
        sender.sendMessage("Czy uruchomiony przez demona: " + (server.isLaunchedViaDaemon() ? "tak" : "nie"));
        sender.sendMessage("Stan serwera: " + server.getServerState());
        final Optional<ServersGroup> serversGroup = server.getServersGroup();
        sender.sendMessage("Grupa serwerów: " + (serversGroup.isPresent() ? serversGroup.get().getName() : "brak"));

        return true;
    }
}

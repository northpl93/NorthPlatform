package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.minigame.MiniGame;

public class NetworkCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (! sender.hasPermission("api.command.network") && ! sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        if (args.length == 0)
        {
            sender.sendMessage("/network - pomoc");
            sender.sendMessage("  proxies - lista połączonych serwerów proxy");
            sender.sendMessage("  daemons - lista połączonych demonów");
            sender.sendMessage("  minigames - lista skonfigurowanych minigier");
            sender.sendMessage("  stopall - wyłącza wszystkie komponenty sieci");
            sender.sendMessage("  kickall - wyrzuca wszystkich graczy");
            return true;
        }

        if (args.length == 1)
        {
            if ("proxies".equals(args[0]))
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPołączone serwery proxy"));
                for (final ProxyInstanceInfo proxyInstanceInfo : API.getNetworkManager().getProxyServers())
                {
                    sender.sendMessage("|- " + proxyInstanceInfo.getId());
                    sender.sendMessage("  |- Liczba graczy: " + proxyInstanceInfo.getOnlinePlayers());
                    sender.sendMessage("  |- Nazwa hosta: " + proxyInstanceInfo.getHostname());
                }
            }
            else if ("daemons".equals(args[0]))
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPołączone demony"));
                for (final RemoteDaemon daemon : API.getNetworkManager().getDaemons())
                {
                    sender.sendMessage("|- " + daemon.getName());
                    sender.sendMessage("  |- Nazwa hosta: " + daemon.getHostName());
                    sender.sendMessage("  |- Maksymalna ilość ramu: " + daemon.getMaxRam() + "MB");
                    sender.sendMessage("  |- Użyty ram: " + daemon.getRamUsed() + "MB (" + daemon.getRamUsed() / daemon.getMaxRam() * 100 + "%)");
                    sender.sendMessage("  |- Ilość serwerów hostowanych: " + daemon.getServerCount());
                }
            }
            else if ("minigames".equals(args[0]))
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSkonfigurowane minigry"));
                for (final MiniGame miniGame : API.getNetworkManager().getMiniGames())
                {
                    sender.sendMessage(" * " + miniGame.getDisplayName() + " (" + miniGame.getSystemName() + ")");
                }
            }
            else if ("stopall".equals(args[0]))
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cZa chwile wszystkie komponenty sieci zostaną wyłączone..."));
                API.getNetworkManager().broadcastNetworkAction(NetworkAction.STOP_ALL);
            }
            else if ("kickall".equals(args[0]))
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cZa chwile wszyscy gracze zostaną rozłączeni..."));
                API.getNetworkManager().broadcastNetworkAction(NetworkAction.KICK_ALL);
            }
            else
            {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cZly argument!"));
            }

            return true;
        }

        // TODO

        return true;
    }
}

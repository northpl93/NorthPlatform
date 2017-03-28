package pl.north93.zgame.api.defaultcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.minigame.MiniGame;

public class Network extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public Network()
    {
        super("network", "net");
        this.setPermission("api.command.network");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            sender.sendMessage("/network - pomoc");
            sender.sendMessage("  proxies - lista połączonych serwerów proxy");
            sender.sendMessage("  daemons - lista połączonych demonów");
            sender.sendMessage("  minigames - lista skonfigurowanych minigier");
            sender.sendMessage("  stopall - wyłącza wszystkie komponenty sieci");
            sender.sendMessage("  kickall - wyrzuca wszystkich graczy");
            return;
        }

        if (args.length() == 1)
        {
            if ("proxies".equals(args.asString(0)))
            {
                sender.sendMessage("&cPołączone serwery proxy");
                for (final ProxyInstanceInfo proxyInstanceInfo : this.networkManager.getProxyServers())
                {
                    sender.sendMessage("|- " + proxyInstanceInfo.getId());
                    sender.sendMessage("  |- Liczba graczy: " + proxyInstanceInfo.getOnlinePlayers());
                    sender.sendMessage("  |- Nazwa hosta: " + proxyInstanceInfo.getHostname());
                }
            }
            else if ("daemons".equals(args.asString(0)))
            {
                sender.sendMessage("&cPołączone demony");
                for (final RemoteDaemon daemon : this.networkManager.getDaemons())
                {
                    sender.sendMessage("|- " + daemon.getName());
                    sender.sendMessage("  |- Nazwa hosta: " + daemon.getHostName());
                    sender.sendMessage("  |- Maksymalna ilość ramu: " + daemon.getMaxRam() + "MB");
                    sender.sendMessage("  |- Użyty ram: " + daemon.getRamUsed() + "MB (" + daemon.getRamUsed() / daemon.getMaxRam() * 100 + "%)");
                    sender.sendMessage("  |- Ilość serwerów hostowanych: " + daemon.getServerCount());
                }
            }
            else if ("minigames".equals(args.asString(0)))
            {
                sender.sendMessage("&cSkonfigurowane minigry");
                for (final MiniGame miniGame : this.networkManager.getMiniGames())
                {
                    sender.sendMessage(" * " + miniGame.getDisplayName() + " (" + miniGame.getSystemName() + ")");
                }
            }
            else if ("stopall".equals(args.asString(0)))
            {
                sender.sendMessage("&cZa chwile wszystkie komponenty sieci zostaną wyłączone...");
                this.networkManager.broadcastNetworkAction(NetworkAction.STOP_ALL);
            }
            else if ("kickall".equals(args.asString(0)))
            {
                sender.sendMessage("&cZa chwile wszyscy gracze zostaną rozłączeni...");
                this.networkManager.broadcastNetworkAction(NetworkAction.KICK_ALL);
            }
            else
            {
                sender.sendMessage("&cZly argument!");
            }

        }

        // TODO
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

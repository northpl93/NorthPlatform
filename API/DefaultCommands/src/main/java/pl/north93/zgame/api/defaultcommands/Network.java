package pl.north93.zgame.api.defaultcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.zgame.api.global.network.proxy.ProxyInstanceInfo;
import pl.north93.zgame.api.global.redis.event.IEventManager;

public class Network extends NorthCommand
{
    @Inject
    private IEventManager eventManager;
    @Inject
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
            sender.sendRawMessage("/network - pomoc");
            sender.sendRawMessage("  proxies - lista połączonych serwerów proxy");
            sender.sendRawMessage("  daemons - lista połączonych demonów");
            sender.sendRawMessage("  stopall - wyłącza wszystkie komponenty sieci");
            sender.sendRawMessage("  kickall - wyrzuca wszystkich graczy");
            return;
        }

        if (args.length() == 1)
        {
            if ("proxies".equals(args.asString(0)))
            {
                sender.sendRawMessage("&cPołączone serwery proxy");
                for (final ProxyInstanceInfo proxyInstanceInfo : this.networkManager.getProxies().getProxyServers())
                {
                    sender.sendRawMessage("|- " + proxyInstanceInfo.getId());
                    sender.sendRawMessage("  |- Liczba graczy: " + proxyInstanceInfo.getOnlinePlayers());
                    sender.sendRawMessage("  |- Nazwa hosta: " + proxyInstanceInfo.getHostname());
                }
            }
            else if ("daemons".equals(args.asString(0)))
            {
                sender.sendRawMessage("&cPołączone demony");
                for (final DaemonDto daemon : this.networkManager.getDaemons().getDaemons())
                {
                    sender.sendRawMessage("|- " + daemon.getName());
                    sender.sendRawMessage("  |- Nazwa hosta: " + daemon.getHostName());
                    sender.sendRawMessage("  |- Maksymalna ilość ramu: " + daemon.getMaxRam() + "MB");
                    sender.sendRawMessage("  |- Użyty ram: " + daemon.getRamUsed() + "MB (" + daemon.getRamUsed() / daemon.getMaxRam() * 100 + "%)");
                    sender.sendRawMessage("  |- Ilość serwerów hostowanych: " + daemon.getServerCount());
                }
            }
            else if ("stopall".equals(args.asString(0)))
            {
                sender.sendRawMessage("&cZa chwile wszystkie komponenty sieci zostaną wyłączone...");
                this.eventManager.callEvent(new NetworkShutdownNetEvent());
            }
            else if ("kickall".equals(args.asString(0)))
            {
                sender.sendRawMessage("&cZa chwile wszyscy gracze zostaną rozłączeni...");
                //this.networkManager.broadcastNetworkAction(NetworkAction.KICK_ALL);
            }
            else
            {
                sender.sendRawMessage("&cZly argument!");
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

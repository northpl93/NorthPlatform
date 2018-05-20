package pl.north93.zgame.features.global.network;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.event.NetworkKickAllNetEvent;
import pl.north93.zgame.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.zgame.api.global.network.proxy.ProxyDto;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.event.IEventManager;

public class NetworkCommand extends NorthCommand
{
    @Inject
    private IEventManager eventManager;
    @Inject
    private INetworkManager networkManager;

    public NetworkCommand()
    {
        super("network", "net");
        this.setPermission("api.command.network");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            sender.sendMessage("&e/network - pomoc");
            sender.sendMessage("&e  proxies - lista połączonych serwerów proxy");
            sender.sendMessage("&e  daemons - lista połączonych demonów");
            sender.sendMessage("&e  servers - lista serwerów");
            sender.sendMessage("&e  kickall - wyrzuca wszystkich graczy");
            sender.sendMessage("&e  stopall - wyłącza wszystkie komponenty sieci");
            return;
        }

        if (args.length() == 1)
        {
            if ("proxies".equals(args.asString(0)))
            {
                sender.sendMessage("&cPołączone serwery proxy");
                for (final ProxyDto proxyInstanceInfo : this.networkManager.getProxies().all())
                {
                    sender.sendMessage("&e|- " + proxyInstanceInfo.getId());
                    sender.sendMessage("&e  |- Liczba graczy: " + proxyInstanceInfo.getOnlinePlayers());
                    sender.sendMessage("&e  |- Nazwa hosta: " + proxyInstanceInfo.getHostname());
                }
            }
            else if ("daemons".equals(args.asString(0)))
            {
                sender.sendMessage("&cPołączone demony");
                for (final DaemonDto daemon : this.networkManager.getDaemons().all())
                {
                    sender.sendMessage("&e|- " + daemon.getName());
                    sender.sendMessage("&e  |- Nazwa hosta: " + daemon.getHostName());
                    sender.sendMessage("&e  |- Maksymalna ilość ramu: " + daemon.getMaxRam() + "MB");

                    final int usedPercent = (int) (daemon.getRamUsed().doubleValue() / daemon.getMaxRam().doubleValue() * 100);
                    sender.sendMessage("&e  |- Użyty ram: " + daemon.getRamUsed() + "MB (" + usedPercent + "%)");

                    sender.sendMessage("&e  |- Ilość serwerów hostowanych: " + daemon.getServerCount());
                }
            }
            else if ("servers".equals(args.asString(0)))
            {
                sender.sendMessage("&cSerwery");
                for (final Server server : this.networkManager.getServers().all())
                {
                    sender.sendMessage("&e|- " + server.getUuid());
                    sender.sendMessage("&e  |- Grupa: " + server.getServersGroup().getName() + " Rodzaj: " + server.getType());
                    sender.sendMessage("&e  |- Stan: " + server.getServerState() + " Gracze: " + server.getPlayersCount());
                }
            }
            else if ("stopall".equals(args.asString(0)))
            {
                sender.sendMessage("&cZa chwile wszystkie komponenty sieci zostaną wyłączone...");
                this.eventManager.callEvent(new NetworkShutdownNetEvent());
            }
            else if ("kickall".equals(args.asString(0)))
            {
                sender.sendMessage("&cZa chwile wszyscy gracze zostaną rozłączeni...");
                this.eventManager.callEvent(new NetworkKickAllNetEvent());
            }
            else
            {
                sender.sendMessage("&cZly argument!");
            }

        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

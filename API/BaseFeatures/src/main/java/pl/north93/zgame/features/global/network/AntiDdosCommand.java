package pl.north93.zgame.features.global.network;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.proxy.AntiDdosConfig;
import pl.north93.zgame.api.global.network.proxy.AntiDdosMode;
import pl.north93.zgame.api.global.network.proxy.ProxyDto;

public class AntiDdosCommand extends NorthCommand
{
    @Inject @NetConfig(type = AntiDdosConfig.class, id = "antiddos")
    private IConfig<AntiDdosConfig> config;
    @Inject
    private INetworkManager networkManager;

    public AntiDdosCommand()
    {
        super("antiddos");
        this.setPermission("basefeatures.cmd.antiddos");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final AntiDdosConfig config = this.config.get();
        if (config == null)
        {
            sender.sendMessage("&cAnty DDoS nie jest skonfigurowany");
            return;
        }

        if (args.isEmpty())
        {
            sender.sendMessage("&aAktualny tryb antyddosa: {0}", config.getMode());
            sender.sendMessage("&eAktualny stan anty ddos na serwerach proxy:");
            for (final ProxyDto proxyDto : this.networkManager.getProxies().all())
            {
                sender.sendMessage("&e  - {0} {1}", proxyDto.getId(), proxyDto.getAntiDdosState());
            }
        }
        else if (args.length() == 1)
        {
            final AntiDdosMode mode = AntiDdosMode.valueOf(args.asString(0));
            this.config.update(update -> update.setMode(mode));
            sender.sendMessage("&aTryb zmieniony na {0}", mode);
        }
        else
        {
            sender.sendMessage("&cJako argument podaj ON, OFF lub AUTO");
        }
    }
}

package pl.north93.northplatform.features.global.network;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.NetworkMeta;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;

public class SlotsCommand extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IProxiesManager proxiesManager;

    public SlotsCommand()
    {
        super("slots");
        this.setPermission("basefeatures.cmd.slots");
        this.setAsync(true); // causes tick drop
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            final NetworkMeta meta = this.networkManager.getNetworkConfig().get();
            sender.sendMessage("&eAktualne sloty: " + meta.displayMaxPlayers + " Gracze: " + this.proxiesManager.onlinePlayersCount());
        }
        else if (args.length() == 1)
        {
            final Integer newSlots = args.asInt(0);
            if (newSlots == null)
            {
                sender.sendMessage("&cPodaj cyfre.");
                return;
            }

            this.networkManager.getNetworkConfig().update(meta ->
            {
                meta.displayMaxPlayers = newSlots;
            });
            sender.sendMessage("&aSloty zmienione na " + newSlots);
        }
        else
        {
            sender.sendMessage("&cPodaj jedna cyfre.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

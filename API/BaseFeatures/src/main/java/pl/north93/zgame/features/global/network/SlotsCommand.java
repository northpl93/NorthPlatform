package pl.north93.zgame.features.global.network;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.NetworkMeta;

public class SlotsCommand extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;

    public SlotsCommand()
    {
        super("slots");
        this.setPermission("basefeatures.cmd.slots");
        this.setAsync(true); // causes tick drop
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            final NetworkMeta meta = this.networkManager.getNetworkConfig().get();
            sender.sendMessage("&eAktualne sloty: " + meta.displayMaxPlayers + " Gracze: " + this.networkManager.getProxies().onlinePlayersCount());
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

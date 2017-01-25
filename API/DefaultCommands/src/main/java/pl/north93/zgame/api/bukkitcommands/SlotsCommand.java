package pl.north93.zgame.api.bukkitcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.network.INetworkManager;

public class SlotsCommand extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public SlotsCommand()
    {
        super("slots");
        this.setPermission("api.command.slots");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            final NetworkMeta meta = this.networkManager.getNetworkMeta().get();
            sender.sendMessage("&eAktualne sloty: " + meta.displayMaxPlayers + " Gracze: " + this.networkManager.onlinePlayersCount());
        }
        else if (args.length() == 1)
        {
            final Integer newSlots = args.asInt(0);
            if (newSlots == null)
            {
                sender.sendMessage("&cPodaj cyfre.");
                return;
            }
            this.networkManager.getNetworkMeta().update(meta ->
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

package pl.north93.zgame.api.defaultcommands;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayerInfoCmd extends NorthCommand
{
    @InjectResource(bundleName = "Commands")
    private ResourceBundle messages;

    public PlayerInfoCmd()
    {
        super("playerinfo", "pinfo");
        this.setPermission("api.command.playerinfo");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 1)
        {
            API.getPlatformConnector().runTaskAsynchronously(() ->
            {
                final Value<IOnlinePlayer> networkPlayer = API.getApiCore().getNetworkManager().getOnlinePlayer(args.asString(0));
                if (!networkPlayer.isCached() && !networkPlayer.isAvailable())
                {
                    final IOfflinePlayer offlinePlayer = API.getApiCore().getNetworkManager().getOfflinePlayer(args.asString(0));
                    if (offlinePlayer == null)
                    {
                        sender.sendMessage(this.messages.getString("command.no_player"));
                        return;
                    }
                    this.printOfflinePlayer(sender, offlinePlayer);
                    return;
                }
                this.printOnlinePlayer(sender, networkPlayer.get());
            });
        }
        else
        {
            sender.sendMessage(this.messages, "command.usage", label, "<nick>");
        }
    }

    private void printOnlinePlayer(final NorthCommandSender sender,  final IOnlinePlayer onlinePlayer)
    {
        sender.sendMessage("Nick: " + onlinePlayer.getNick() + " (online)");
        sender.sendMessage("UUID: " + onlinePlayer.getUuid());
        sender.sendMessage("Połączenie: " + onlinePlayer.getProxyId() + " <-> " + onlinePlayer.getServerId());
        sender.sendMessage("Ranga: " + onlinePlayer.getGroup().getName());
    }

    private void printOfflinePlayer(final NorthCommandSender sender, final IOfflinePlayer offlinePlayer)
    {
        sender.sendMessage("Latest known username: " + offlinePlayer.getLatestNick());
        sender.sendMessage("UUID: " + offlinePlayer.getUuid());
        sender.sendMessage("Ranga: " + offlinePlayer.getGroup().getName());
    }
}

package pl.north93.zgame.api.defaultcommands;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.NetworkPlayer;

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
                final NetworkPlayer networkPlayer = API.getApiCore().getNetworkManager().getNetworkPlayer(args.asString(0));
                if (networkPlayer == null)
                {
                    sender.sendMessage(this.messages.getString("command.no_player"));
                    return;
                }
                sender.sendMessage("Nick: " + networkPlayer.getNick());
                sender.sendMessage("UUID: " + networkPlayer.getUuid());
                sender.sendMessage("Połączenie: " + networkPlayer.getProxyId() + " <-> " + networkPlayer.getServerId());
                sender.sendMessage("Ranga: " + networkPlayer.getGroup().getName());
            });
        }
        else
        {
            sender.sendMessage(this.messages, "command.usage", label, "<nick>");
        }
    }
}

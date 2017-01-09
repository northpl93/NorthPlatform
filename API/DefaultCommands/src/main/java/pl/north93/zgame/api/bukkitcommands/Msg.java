package pl.north93.zgame.api.bukkitcommands;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class Msg extends NorthCommand
{
    @InjectResource(bundleName = "Commands")
    private ResourceBundle messages;

    public Msg()
    {
        super("msg", "pw");
        this.setPermission("api.command.msg");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 2)
        {
            sender.sendMessage(this.messages, "command.usage", label, "<nick gracza> <wiadomosc>");
            return;
        }

        API.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Value<IOnlinePlayer> networkPlayer = API.getApiCore().getNetworkManager().getOnlinePlayer(args.asString(0));
            if (!networkPlayer.isCached() && !networkPlayer.isAvailable())
            {
                sender.sendMessage(this.messages, "command.no_player");
                return;
            }

            final String message = args.asText(1);
            sender.sendMessage(this.messages, "command.msg.message", this.messages.getString("command.msg.you"), networkPlayer.get().getNick(), message);
            networkPlayer.get().sendMessage(this.messages, "command.msg.message", sender.getName(), "ty", message);
        });
    }
}

package pl.north93.zgame.api.defaultcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;

public class PlayerInfoCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectMessages("Commands")
    private MessagesBox     messages;

    public PlayerInfoCmd()
    {
        super("playerinfo", "pinfo");
        this.setPermission("api.command.playerinfo");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 1)
        {
            boolean success = this.networkManager.getPlayers().access(
                    args.asString(0),
                    online -> this.printOnlinePlayer(sender, online),
                    offline -> this.printOfflinePlayer(sender, offline));

            if (! success)
            {
                sender.sendMessage(this.messages, "command.no_player");
            }
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

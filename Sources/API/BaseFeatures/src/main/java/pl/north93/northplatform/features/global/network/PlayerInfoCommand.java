package pl.north93.northplatform.features.global.network;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;

public class PlayerInfoCommand extends NorthCommand
{
    @Inject
    private IPlayersManager playersManager;
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;

    public PlayerInfoCommand()
    {
        super("playerinfo", "pinfo");
        this.setPermission("basefeatures.cmd.playerinfo");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 1)
        {
            final boolean success = this.playersManager.access(
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

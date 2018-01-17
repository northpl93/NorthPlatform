package pl.north93.zgame.api.bukkitcommands;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;

public class Kick extends NorthCommand
{
    @Inject
    private ApiCore         apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject @Messages("Commands")
    private MessagesBox     messages;

    public Kick()
    {
        super("kick");
        this.setPermission("api.command.kick");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 1)
        {
            sender.sendMessage(this.messages, "command.usage", label, "<nick gracza> [wiadomosc wyrzucenia]");
            return;
        }

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(args.asString(0)))
        {
            if (! t.isOnline())
            {
                sender.sendMessage(this.messages, "command.no_player");
                return;
            }
            final IOnlinePlayer player = (IOnlinePlayer) t.getPlayer();

            final String reason = args.asText(1);
            final String kickMessage;
            if (StringUtils.isEmpty(reason))
            {
                kickMessage = this.messages.getMessage(player.getMyLocale(), "kick.by_command.without_reason");
            }
            else
            {
                kickMessage = MessageFormat.format(this.messages.getMessage(player.getMyLocale(), "kick.by_command.with_reason"), reason);
            }

            player.kick(kickMessage);
        }
        catch (final Exception e)
        {
            sender.sendMessage(this.messages, "command.no_player");
        }
    }
}

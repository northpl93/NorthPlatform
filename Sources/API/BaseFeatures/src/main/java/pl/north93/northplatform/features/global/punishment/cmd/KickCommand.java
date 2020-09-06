package pl.north93.northplatform.features.global.punishment.cmd;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;

public class KickCommand extends NorthCommand
{
    @Inject
    private IPlayersManager playersManager;
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;

    public KickCommand()
    {
        super("kick");
        this.setPermission("basefeatures.cmd.kick");
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

        try (final IPlayerTransaction t = this.playersManager.transaction(args.asString(0)))
        {
            if (! t.isOnline())
            {
                sender.sendMessage(this.messages, "command.no_player");
                return;
            }
            final IOnlinePlayer player = t.getPlayer();

            final String reason = args.asText(1);
            final String kickMessage;
            if (StringUtils.isEmpty(reason))
            {
                kickMessage = this.messages.getString(player.getMyLocale(), "kick.by_command.without_reason");
            }
            else
            {
                kickMessage = MessageFormat.format(this.messages.getString(player.getMyLocale(), "kick.by_command.with_reason"), reason);
            }

            player.kick(ChatUtils.fromLegacyText(kickMessage));
        }
        catch (final Exception e)
        {
            sender.sendMessage(this.messages, "command.no_player");
        }
    }
}

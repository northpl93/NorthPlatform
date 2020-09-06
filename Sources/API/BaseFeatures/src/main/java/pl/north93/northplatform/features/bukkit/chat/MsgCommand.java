package pl.north93.northplatform.features.bukkit.chat;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.redis.observable.Value;

public class MsgCommand extends NorthCommand
{
    @Inject
    private IPlayersManager playersManager;
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;

    private static final MetaKey LAST_SENDER = MetaKey.get("lastMessageSender");

    public MsgCommand()
    {
        super("msg", "pw", "pm", "tell", "whisper", "w");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 2)
        {
            sender.sendMessage(this.messages, "command.usage", label, "<nick gracza> <wiadomosc>");
            return;
        }

        final Player player = (Player) sender.unwrapped();

        // todo rewrite
        final Value<IOnlinePlayer> networkPlayer = this.playersManager.unsafe().getOnlineValue(args.asString(0));
        if (! networkPlayer.isPreset())
        {
            sender.sendMessage(this.messages, "command.no_player");
            return;
        }

        final IOnlinePlayer receiver = networkPlayer.get();
        if (! player.hasPermission("api.privatemessagespolicy.ignore"))
        {
            switch (receiver.privateMessagesPolicy())
            {
                case DISABLED:
                    sender.sendMessage(this.messages, "command.msg.disabled");
                    return;
                case FROM_FRIENDS:
                default:
                    // TODO
                    // return;
                    break;
                    
                
            }
        }

        final boolean colorText = player.hasPermission("api.command.msg.colorize");
        final String message = colorText ? args.asText(1) : StringUtils.replace(args.asText(1), "&", "");
        sender.sendMessage(this.messages, "command.msg.from_you_message", receiver.getNick(), message);
        receiver.sendMessage(this.messages, "command.msg.to_you_message", sender.getName(), message);

        this.playersManager.access(receiver.getNick(), iPlayer ->
        {
            final MetaStore metaStore = iPlayer.getMetaStore();
            metaStore.set(LAST_SENDER, sender.getName());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

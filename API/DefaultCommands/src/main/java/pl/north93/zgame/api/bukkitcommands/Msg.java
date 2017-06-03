package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class Msg extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    @Messages("Commands")
    private MessagesBox     messages;

    private static final MetaKey LAST_SENDER = MetaKey.get("lastMessageSender");

    public Msg()
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

        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getOnlinePlayer(args.asString(0));
        if (!networkPlayer.isCached() && !networkPlayer.isAvailable())
        {
            sender.sendMessage(this.messages, "command.no_player");
            return;
        }

        final IOnlinePlayer receiver = networkPlayer.get();
        if(! ((Player) sender.unwrapped()).hasPermission("api.privatemessagespolicy.ignore"))
        {
            switch (receiver.privateMessagesPolicy()) {
                case DISABLED:
                    sender.sendMessage(this.messages, "command.msg.disabled");
                    return;
                case FROM_FRIENDS:
                    // TODO
                    // return;
                    break;
            }
        }

        final boolean colorText = ((Player) sender.unwrapped()).hasPermission("api.command.msg.colorize");
        final String message = colorText ? args.asText(1) : StringUtils.replace(args.asText(1), "&", "");
        sender.sendMessage(this.messages, "command.msg.message", this.messages.getMessage("command.msg.you"), receiver.getNick(), message);
        receiver.sendMessage(this.messages, "command.msg.message", sender.getName(), this.messages.getMessage("command.msg.you"), message);

        this.networkManager.getPlayers().access(receiver.getNick(), iPlayer ->
        {
            final MetaStore metaStore = iPlayer.getMetaStore();
            metaStore.setString(LAST_SENDER, sender.getName());
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

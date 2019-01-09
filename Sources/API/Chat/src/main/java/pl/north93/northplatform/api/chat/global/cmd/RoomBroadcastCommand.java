package pl.north93.northplatform.api.chat.global.cmd;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.chat.global.ChatManager;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.chat.global.ChatRoomNotFoundException;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class RoomBroadcastCommand extends NorthCommand
{
    @Inject
    @Messages("ChatApi")
    private MessagesBox messages;
    @Inject
    private ChatManager chatManager;

    public RoomBroadcastCommand()
    {
        super("roombroadcast", "rbroad");
        this.setPermission("chat.cmd.roombroadcast");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 2)
        {
            sender.sendMessage("&c/{0} <id_pokoju> <wiadomosc>", label);
            return;
        }

        final ChatRoom room;
        try
        {
            room = this.chatManager.getRoom(args.asString(0));
        }
        catch (final ChatRoomNotFoundException e)
        {
            sender.sendMessage(this.messages, "broadcast.no_room", args.asString(0));
            return;
        }

        final Locale locale = sender.getMyLocale();
        final String text = args.asText(1);

        final BaseComponent message = this.messages.getComponent(locale, "broadcast.prefix", text);
        room.broadcast(MessageLayout.SEPARATED.processMessage(message));

        sender.sendMessage("&aWyslano ogloszenie do pokoju {0}", room.getId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

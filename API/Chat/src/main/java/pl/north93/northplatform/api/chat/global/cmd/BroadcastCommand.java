package pl.north93.northplatform.api.chat.global.cmd;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.chat.global.ChatManager;
import pl.north93.northplatform.api.chat.global.ChatRoom;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class BroadcastCommand extends NorthCommand
{
    @Inject
    @Messages("ChatApi")
    private MessagesBox messages;
    @Inject
    private ChatManager chatManager;

    public BroadcastCommand()
    {
        super("broadcast", "broad");
        this.setPermission("chat.cmd.broadcast");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            sender.sendMessage("&cPodaj tresc wiadomosci. Wiadomosc zawiera tylko prefix, domyslnie jest biala.");
            return;
        }

        final ChatRoom rootRoom = this.chatManager.getRootRoom();

        final Locale locale = sender.getMyLocale();
        final String text = args.asText(0);

        final BaseComponent message = this.messages.getComponent(locale, "broadcast.prefix", text);
        rootRoom.broadcast(MessageLayout.SEPARATED.processMessage(message));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

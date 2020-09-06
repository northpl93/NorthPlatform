package pl.north93.northplatform.features.bukkit.chat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.PrivateMessages;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;

public class IgnoreMsgCommand extends NorthCommand
{
    @Inject
    private IPlayersManager playersManager;
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;

    public IgnoreMsgCommand()
    {
        super("ignoremsg");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.playersManager.access(sender.getName(), player ->
        {
            if (player.privateMessagesPolicy() == PrivateMessages.ENABLED)
            {
                player.setPrivateMessagesPolicy(PrivateMessages.DISABLED);
                sender.sendMessage(this.messages, "command.ignoremsg.disabled");
            }
            else if (player.privateMessagesPolicy() == PrivateMessages.DISABLED)
            {
                player.setPrivateMessagesPolicy(PrivateMessages.ENABLED);
                sender.sendMessage(this.messages, "command.ignoremsg.enabled");
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.zgame.api.bukkitcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.PrivateMessages;

public class IgnoreMsgCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectMessages("Commands")
    private MessagesBox     messages;

    public IgnoreMsgCmd()
    {
        super("ignoremsg");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.networkManager.getPlayers().access(sender.getName(), player ->
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

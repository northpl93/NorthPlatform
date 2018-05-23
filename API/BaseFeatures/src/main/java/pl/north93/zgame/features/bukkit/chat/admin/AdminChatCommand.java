package pl.north93.zgame.features.bukkit.chat.admin;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class AdminChatCommand extends NorthCommand
{
    @Inject
    private AdminChatService adminChatService;

    public AdminChatCommand()
    {
        super("adminchat", "ac");
        this.setPermission("admin.chatroom");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final String message = args.asText(0);

        final Player bukkitPlayer = (Player) sender.unwrapped();
        final INorthPlayer player = INorthPlayer.wrap(bukkitPlayer);

        final ChatRoom adminRoom = this.adminChatService.getAdminRoom();
        final BaseComponent component = adminRoom.getChatFormatter().format(player, message);

        adminRoom.broadcast(component);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

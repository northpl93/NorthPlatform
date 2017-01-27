package pl.north93.zgame.webauth.server.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.webauth.server.WebAuthServerComponent;

public class ZalogujsieCmd extends NorthCommand
{
    @InjectComponent("WebAuth.Server")
    private WebAuthServerComponent webAuthServer;

    public ZalogujsieCmd()
    {
        super("zalogujsie");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final String loginUrl = this.webAuthServer.getWebAuthManager().getLoginUrl(player.getUniqueId());

        final TextComponent message = new TextComponent("Kliknij tutaj, aby zalogowac sie na stronie.");
        message.setColor(ChatColor.GREEN);
        message.setUnderlined(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, loginUrl));
        player.spigot().sendMessage(message);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.northplatform.auth.server.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.auth.api.IAuthManager;
import pl.north93.northplatform.auth.api.IAuthPlayer;

@Slf4j
public class ChangePasswordCommand extends NorthCommand
{
    @Inject @Messages("NoPremiumAuth")
    private MessagesBox  messages;
    @Inject
    private IAuthManager authManager;

    public ChangePasswordCommand()
    {
        super("changepassword", "zmienhaslo");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final IAuthPlayer authPlayer = this.authManager.getPlayer(Identity.of(player));

        if (authPlayer.isPremium() || ! authPlayer.isRegistered())
        {
            sender.sendMessage(this.messages, "error.premium_cant_change_password");
            return;
        }

        if (args.length() != 2)
        {
            sender.sendMessage(this.messages, "cmd.changepassword.args", label);
            return;
        }

        if (! authPlayer.checkPassword(args.asString(0)))
        {
            log.info("User {} specified invalid old password (no-premium password)", player.getName());
            sender.sendMessage(this.messages, "cmd.changepassword.old_password_not_match");
            return;
        }

        authPlayer.setPassword(args.asString(1));
        log.info("User {} successfully changed password! (no-premium password)", player.getName());
        sender.sendMessage(this.messages, "cmd.changepassword.success");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

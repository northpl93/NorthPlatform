package pl.north93.zgame.auth.server.cmd;

import static java.text.MessageFormat.format;


import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.auth.api.IAuthManager;
import pl.north93.zgame.auth.api.IAuthPlayer;

public class ChangePasswordCommand extends NorthCommand
{
    @Inject @Messages("NoPremiumAuth")
    private MessagesBox  messages;
    @Inject
    private IAuthManager authManager;
    @Inject
    private Logger       logger;

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
            this.logger.info(format("User {0} specified invalid old password (no-premium password)", player.getName()));
            sender.sendMessage(this.messages, "cmd.changepassword.old_password_not_match");
            return;
        }

        authPlayer.setPassword(args.asString(1));
        this.logger.info(format("User {0} successfully changed password! (no-premium password)", player.getName()));
        sender.sendMessage(this.messages, "cmd.changepassword.success");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

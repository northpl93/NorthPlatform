package pl.north93.northplatform.auth.sharedimpl.cmd;


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
public class AdminChangePasswordCommand extends NorthCommand
{
    @Inject @Messages("NoPremiumAuth")
    private MessagesBox  messages;
    @Inject
    private IAuthManager authManager;

    public AdminChangePasswordCommand()
    {
        super("adminchangepassword", "achangepassword");
        this.setPermission("nopremiumauth.cmd.adminchangepassword");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2)
        {
            sender.sendMessage(this.messages, "cmd.adminchangepassword.args", label);
            return;
        }

        final String nick = args.asString(0);

        final IAuthPlayer player = this.authManager.getPlayer(Identity.create(null, nick));
        if (player.isPremium())
        {
            log.info("Admin tried to change password of premium user {} (no-premium password)", nick);
            sender.sendMessage(this.messages, "cmd.adminchangepassword.premium", nick);
            return;
        }

        player.setPassword(args.asString(1));
        log.info("Admin successfully changed password for {} (no-premium password)", nick);
        sender.sendMessage(this.messages, "cmd.adminchangepassword.success", nick);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

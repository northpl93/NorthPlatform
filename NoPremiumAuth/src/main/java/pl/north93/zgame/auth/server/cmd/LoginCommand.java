package pl.north93.zgame.auth.server.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.auth.api.IAuthManager;
import pl.north93.zgame.auth.api.IAuthPlayer;
import pl.north93.zgame.auth.server.event.PlayerSuccessfullyAuthEvent;

public class LoginCommand extends NorthCommand
{
    private final Logger logger = LoggerFactory.getLogger(LoginCommand.class);
    @Inject @Messages("NoPremiumAuth")
    private MessagesBox  messages;
    @Inject
    private IAuthManager authManager;

    public LoginCommand()
    {
        super("login", "l", "zaloguj");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        if (this.authManager.isLoggedIn(player.getName()))
        {
            sender.sendMessage(this.messages, "error.already_logged_in");
            return;
        }

        final IAuthPlayer authPlayer = this.authManager.getPlayer(Identity.of(player));
        if (! authPlayer.isRegistered())
        {
            sender.sendMessage(this.messages, "error.register_first");
            return;
        }

        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.login.args", label);
            return;
        }

        if (! authPlayer.checkPassword(args.asString(0)))
        {
            this.logger.info("User {} specified invalid password!", player.getName());
            sender.sendMessage(this.messages, "error.password_does_not_match");
            return;
        }

        this.authManager.setLoggedInStatus(Identity.of(player), true);
        this.logger.info("User {} successfully logged-in! (no-premium password)", player.getName());
        sender.sendMessage(this.messages, "info.successfully_logged");
        Bukkit.getPluginManager().callEvent(new PlayerSuccessfullyAuthEvent(player)); // todo zrobić fasadę na Bukkitową część API
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

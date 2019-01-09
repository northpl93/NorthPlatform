package pl.north93.northplatform.auth.server.cmd;

import org.bukkit.Bukkit;
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
import pl.north93.northplatform.auth.server.event.PlayerSuccessfullyAuthEvent;

@Slf4j
public class LoginCommand extends NorthCommand
{
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
            log.info("User {} specified invalid password!", player.getName());
            sender.sendMessage(this.messages, "error.password_does_not_match");
            return;
        }

        this.authManager.setLoggedInStatus(Identity.of(player), true);
        log.info("User {} successfully logged-in! (no-premium password)", player.getName());
        sender.sendMessage(this.messages, "info.successfully_logged");
        Bukkit.getPluginManager().callEvent(new PlayerSuccessfullyAuthEvent(player)); // todo zrobić fasadę na Bukkitową część API
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

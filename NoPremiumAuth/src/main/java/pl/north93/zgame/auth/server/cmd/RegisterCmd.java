package pl.north93.zgame.auth.server.cmd;

import static java.text.MessageFormat.format;


import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mindrot.jbcrypt.BCrypt;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.auth.api.player.AuthPlayer;
import pl.north93.zgame.auth.server.AuthServerComponent;
import pl.north93.zgame.auth.sharedimpl.AuthManagerImpl;

public class RegisterCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectResource(bundleName = "NoPremiumAuth")
    private ResourceBundle      messages;
    @InjectComponent("NoPremiumAuth.Server")
    private AuthServerComponent authServer;
    private Logger              logger;

    public RegisterCmd()
    {
        super("register", "zarejestruj");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final AuthManagerImpl authManager = this.authServer.getAuthManager();
        final Player player = (Player) sender.unwrapped();
        if (authManager.isLoggedIn(player.getUniqueId()))
        {
            sender.sendMessage(this.messages, "error.already_logged_in");
            return;
        }

        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(player.getName());
        final AuthPlayer authPlayer = AuthPlayer.get(onlinePlayer);
        if (authPlayer.isRegistered())
        {
            sender.sendMessage(this.messages, "error.already_registered");
            return;
        }

        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.login.args", label);
            return;
        }

        final String password = BCrypt.hashpw(args.asString(0), BCrypt.gensalt());
        authPlayer.setPassword(password);
        authManager.setLoggedInStatus(player.getUniqueId(), true);
        this.logger.info(format("User {0} successfully registered! (no-premium password)", player.getName()));
        sender.sendMessage(this.messages, "info.successfully_registered");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

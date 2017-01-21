package pl.north93.zgame.auth.server.cmd;

import java.util.ResourceBundle;

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

public class LoginCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectResource(bundleName = "NoPremiumAuth")
    private ResourceBundle      messages;
    @InjectComponent("NoPremiumAuth.Server")
    private AuthServerComponent authServer;

    public LoginCmd()
    {
        super("login", "l", "zaloguj");
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

        if (! BCrypt.checkpw(args.asString(0), authPlayer.getPassword()))
        {
            sender.sendMessage(this.messages, "error.password_does_not_match");
            return;
        }

        authManager.setLoggedInStatus(player.getUniqueId(), true);
        sender.sendMessage(this.messages, "info.successfully_logged");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

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

public class ChangePasswordCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectResource(bundleName = "NoPremiumAuth")
    private ResourceBundle      messages;
    @InjectComponent("NoPremiumAuth.Server")
    private AuthServerComponent authServer;

    public ChangePasswordCmd()
    {
        super("changepassword", "zmienhaslo");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(player.getName());
        final AuthPlayer authPlayer = AuthPlayer.get(onlinePlayer);

        if (! authPlayer.isRegistered())
        {
            sender.sendMessage(this.messages, "error.premium_cant_change_password");
            return;
        }

        if (args.length() != 2)
        {
            sender.sendMessage(this.messages, "cmd.changepassword.args", label);
            return;
        }

        if (! BCrypt.checkpw(args.asString(0), authPlayer.getPassword()))
        {
            sender.sendMessage(this.messages, "cmd.changepassword.old_password_not_match");
            return;
        }

        authPlayer.setPassword(BCrypt.hashpw(args.asString(1), BCrypt.gensalt()));
        sender.sendMessage(this.messages, "cmd.changepassword.success");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

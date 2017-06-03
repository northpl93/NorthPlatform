package pl.north93.zgame.api.bukkitcommands;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;


public class TempbanCommand extends NorthCommand {
    @Inject
    private INetworkManager networkManager;

    private static final MetaKey BAN_EXPIRE = MetaKey.get("banExpireAt");

    public TempbanCommand()
    {
        super("tempban", "tban", "cban");
        this.setPermission("api.command.ban");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2 || !StringUtils.isNumeric(args.asString(1)))
        {
            sender.sendMessage("&c/tban nick czas(w sekundach)");
            return;
        }

        final long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(args.asLong(1));

        this.networkManager.getPlayers().access(args.asString(0), online ->
        {
            online.setBanned(true);
            online.getMetaStore().setLong(BAN_EXPIRE, expireAt);
            online.kick(ChatColor.RED + "Zostales zbanowany na " + args.asString(1) + " sekund!");
        }, offline ->
        {
            offline.setBanned(true);
            offline.getMetaStore().setLong(BAN_EXPIRE, expireAt);
        });

        sender.sendMessage("&cUzytkownik zbanowany na " + args.asString(1) + " sekund.");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkManager", this.networkManager).toString();
    }
}

package pl.north93.zgame.api.bukkitcommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;

public class UnbanCommand extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;

    private static final MetaKey BAN_EXPIRE = MetaKey.get("banExpireAt");

    public UnbanCommand()
    {
        super("unban");
        this.setPermission("api.command.unban");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendRawMessage("&c/unban nick");
            return;
        }
        this.networkManager.getPlayers().access(args.asString(0), player ->
        {
            player.setBanned(false);
            player.getMetaStore().remove(BAN_EXPIRE);
        });

        sender.sendRawMessage("&cUzytkownik odbanowany");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

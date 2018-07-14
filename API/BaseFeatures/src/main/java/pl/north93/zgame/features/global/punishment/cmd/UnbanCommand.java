package pl.north93.zgame.features.global.punishment.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.features.global.punishment.BanService;

public class UnbanCommand extends NorthCommand
{
    @Inject
    private BanService      banService;

    public UnbanCommand()
    {
        super("unban");
        this.setPermission("basefeatures.cmd.unban");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage("&c/unban nick");
            return;
        }

        try
        {
            this.banService.removeBan(Identity.create(null, args.asString(0)));
            sender.sendMessage("&cUzytkownik odbanowany");
        }
        catch (final PlayerNotFoundException e)
        {
            sender.sendMessage("&cNie znaleziono uzytkownika {0}", args.asString(0));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

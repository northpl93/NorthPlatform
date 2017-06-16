package pl.north93.zgame.datashare.server.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class AnnCmd extends NorthCommand
{
    @Inject
    private PlayerDataShareServer dataShareServer;
    @Inject
    private PlayerDataShareComponent dataShareComponent;

    public AnnCmd()
    {
        super("ann");
        this.setPermission("api.command.groupbroadcast");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final DataSharingGroup myGroup = this.dataShareServer.getMyGroup();
        if (args.length() == 0)
        {
            sender.sendRawMessage("&cTa komenda pokazuje tekst na środku ekranu graczy w aktualnej grupie serwerów (" + myGroup.getName() + ")");
        }
        else
        {
            this.dataShareComponent.getDataShareManager().ann(myGroup, ChatColor.translateAlternateColorCodes('&', args.asText()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

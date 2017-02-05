package pl.north93.zgame.datashare.server.cmd;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class GroupBroadcastCmd extends NorthCommand
{
    @InjectComponent("PlayerDataShare.Bukkit")
    private PlayerDataShareServer    dataShareServer;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent dataShareComponent;

    public GroupBroadcastCmd()
    {
        super("groupbroadcast", "gbroadcast", "gb");
        this.setPermission("api.command.groupbroadcast");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final DataSharingGroup myGroup = this.dataShareServer.getMyGroup();
        if (args.length() == 0)
        {
            sender.sendMessage("&cTa komenda wysyła wiadomość w aktualnej grupie serwerów (" + myGroup.getName() + ")");
        }
        else
        {
            this.dataShareComponent.getDataShareManager().broadcast(myGroup, args.asText());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

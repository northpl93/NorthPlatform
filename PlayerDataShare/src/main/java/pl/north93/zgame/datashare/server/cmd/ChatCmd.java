package pl.north93.zgame.datashare.server.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareManager;
import pl.north93.zgame.datashare.server.PlayerDataShareServer;
import pl.north93.zgame.datashare.sharedimpl.PlayerDataShareComponent;

public class ChatCmd extends NorthCommand
{
    @InjectComponent("PlayerDataShare.Bukkit")
    private PlayerDataShareServer    dataShareServer;
    @InjectComponent("PlayerDataShare.SharedImpl")
    private PlayerDataShareComponent dataShareComponent;

    public ChatCmd()
    {
        super("chat");
        this.setPermission("api.command.chat");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final DataSharingGroup myGroup = this.dataShareServer.getMyGroup();
        final IDataShareManager manager = this.dataShareComponent.getDataShareManager();
        final boolean chatEnabled = manager.isChatEnabled(myGroup);

        if (args.length() == 0)
        {
            sender.sendMessage("&eZarządzanie czatem w grupie " + myGroup.getName());
            sender.sendMessage("&eCzat jest: " + (chatEnabled ? "&awłączony" : "&cwyłączony"));
            sender.sendMessage("&e/chat switch - przełącz stan czatu");
            sender.sendMessage("&e/gbroadcast - ogłoś wiadomość w tej grupie serwerów");
        }
        else if (args.length() == 1)
        {
            if (args.asString(0).equalsIgnoreCase("switch"))
            {
                manager.setChatEnabled(myGroup, !chatEnabled);
                sender.sendMessage("&aZrobione!");
            }
            else
            {
                sender.sendMessage("&cZłe parametry komendy.");
            }
        }
        else
        {
            sender.sendMessage("&cZłe parametry komendy.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.zgame.datashare.server.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.JoiningPolicy;
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
        final JoiningPolicy currentPolicy = manager.getChatPolicy(myGroup);

        if (args.length() == 0)
        {
            sender.sendMessage("&eZarządzanie czatem w grupie " + myGroup.getName());
            sender.sendMessage("&eChatPolicy: " + currentPolicy);
            sender.sendMessage("&e/chat switch - przełącz stan czatu");
            sender.sendMessage("&e/gbroadcast - ogłoś wiadomość w tej grupie serwerów");
            sender.sendMessage("&e/ann - wiadomość na środku ekranu wszystkich graczy");
        }
        else if (args.length() == 1)
        {
            if (args.asString(0).equalsIgnoreCase("switch"))
            {
                sender.sendMessage("&eUzyj: /chat policy [VIP/ADMIN/EVERYONE]");
            }
            else if (args.asString(0).equalsIgnoreCase("policy"))
            {
                sender.sendMessage("&eUzyj: /chat policy [VIP/ADMIN/EVERYONE]");
            }
            else if (args.asString(0).equalsIgnoreCase("clear"))
            {
                for (int i = 0; i < 100; i++)
                {
                    this.dataShareComponent.getDataShareManager().broadcast(myGroup, " ");
                }
            }
            else
            {
                sender.sendMessage("&cZłe parametry komendy.");
            }
        }
        else if (args.length() == 2)
        {
            if (args.asString(0).equalsIgnoreCase("policy"))
            {
                final JoiningPolicy newPolicy;
                switch(args.asString(1).toLowerCase())
                {
                    case "vip": newPolicy = JoiningPolicy.ONLY_VIP; break;
                    case "nobody":
                    case "admin": newPolicy = JoiningPolicy.ONLY_ADMIN; break;
                    default: newPolicy = JoiningPolicy.EVERYONE;
                }

                manager.setChatPolicy(myGroup, newPolicy);
                sender.sendMessage("&2Ustawiono ChatPolicy na: " + newPolicy);
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
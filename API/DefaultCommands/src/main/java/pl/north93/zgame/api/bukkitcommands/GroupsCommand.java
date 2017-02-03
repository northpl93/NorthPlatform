package pl.north93.zgame.api.bukkitcommands;

import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;

public class GroupsCommand extends NorthCommand
{
    private ApiCore            apiCore;
    @InjectComponent("API.MinecraftNetwork.PermissionsManager")
    private PermissionsManager permissionsManager;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager    networkManager;
    @InjectResource(bundleName = "Commands")
    private ResourceBundle     messages;

    public GroupsCommand()
    {
        super("groups", "group");
        this.setPermission("api.command.groups");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            if (args.length() == 0)
            {
                sender.sendMessage("&e/" + label + " <gracz> - wyświetla grupę gracza");
                sender.sendMessage("&e/" + label + " <gracz> <grupa> - zmienia grupę gracza");
            }
            else if (args.length() == 1)
            {
                final String username = args.asString(0);

                boolean result = this.networkManager.getPlayers().access(username, online ->
                {
                    sender.sendMessage("&eGrupa " + online.getNick() + " to " + online.getGroup().getName());
                }, offline ->
                {
                    sender.sendMessage("&eGrupa " + offline.getLatestNick() + " (" + offline.getUuid() + ") to " + offline.getGroup().getName());
                });

                if (! result)
                {
                    sender.sendMessage(this.messages, "command.no_player");
                }
            }
            else if (args.length() == 2)
            {
                final String username = args.asString(0);
                final Group newGroup = this.permissionsManager.getGroupByName(args.asString(1));
                if (newGroup == null)
                {
                    sender.sendMessage("&cNie ma takiej grupy!");
                    return;
                }
                if (this.networkManager.getPlayers().access(username, player -> player.setGroup(newGroup)))
                {
                    sender.sendMessage("&aPomyślnie zmieniono grupę na " + newGroup.getName());
                }
                else
                {
                    sender.sendMessage(this.messages, "command.no_player");
                }
            }
            else
            {
                sender.sendMessage("&cZła ilość argumentów!");
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

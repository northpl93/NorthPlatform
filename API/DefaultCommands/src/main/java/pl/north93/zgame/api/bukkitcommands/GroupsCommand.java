package pl.north93.zgame.api.bukkitcommands;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.observable.Value;

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
                final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(username);
                if (onlinePlayer.isAvailable())
                {
                    final IOnlinePlayer iOnlinePlayer = onlinePlayer.get();
                    sender.sendMessage("&eGrupa " + iOnlinePlayer.getNick() + " to " + iOnlinePlayer.getGroup().getName());
                }
                else
                {
                    final IOfflinePlayer offlinePlayer = this.networkManager.getOfflinePlayer(username);
                    if (offlinePlayer == null)
                    {
                        sender.sendMessage(this.messages, "command.no_player");
                        return;
                    }
                    sender.sendMessage("&eGrupa " + offlinePlayer.getLatestNick() + " (" + offlinePlayer.getUuid() + ") to " + offlinePlayer.getGroup().getName());
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

                final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(username);
                if (! onlinePlayer.update(iOnlinePlayer ->
                {
                    iOnlinePlayer.setGroup(newGroup);
                    iOnlinePlayer.sendMessage(this.messages, "command.groups.changed", newGroup.getName());
                }))
                {
                    final IOfflinePlayer offlinePlayer = this.networkManager.getOfflinePlayer(username);
                    if (offlinePlayer == null)
                    {
                        sender.sendMessage(this.messages, "command.no_player");
                        return;
                    }
                    offlinePlayer.setGroup(newGroup);
                    this.networkManager.savePlayer(offlinePlayer);
                }

                sender.sendMessage("&aPomyślnie zmieniono grupę na " + newGroup.getName());
            }
            else
            {
                sender.sendMessage("&cZła ilość argumentów!");
            }
        });
    }
}

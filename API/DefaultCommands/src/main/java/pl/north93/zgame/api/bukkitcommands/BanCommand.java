package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.ChatColor;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;

public class BanCommand extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public BanCommand()
    {
        super("ban");
        this.setPermission("api.command.ban");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage("&c/ban nick");
            return;
        }
        this.networkManager.getPlayers().access(args.asString(0), online ->
        {
            online.setBanned(true);
            online.kick(ChatColor.RED + "Zostales zbanowany!");
        }, offline ->
        {
            offline.setBanned(true);
        });

        sender.sendMessage("&cUzytkownik zbanowany");
    }
}

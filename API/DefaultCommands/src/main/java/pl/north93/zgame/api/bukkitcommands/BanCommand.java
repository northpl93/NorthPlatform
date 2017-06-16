package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.ChatColor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;

public class BanCommand extends NorthCommand
{
    @Inject
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
            sender.sendRawMessage("&c/ban nick");
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

        sender.sendRawMessage("&cUzytkownik zbanowany");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkManager", this.networkManager).toString();
    }
}

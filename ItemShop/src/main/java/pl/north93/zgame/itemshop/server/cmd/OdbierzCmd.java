package pl.north93.zgame.itemshop.server.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.itemshop.server.ItemShopServer;
import pl.north93.zgame.itemshop.server.gui.ReceiveGui;
import pl.north93.zgame.itemshop.shared.ReceiveStorage;

public class OdbierzCmd extends NorthCommand
{
    private BukkitApiCore  bukkitApiCore;
    @InjectComponent("ItemShop.Server")
    private ItemShopServer itemShopServer;

    public OdbierzCmd()
    {
        super("odbierz");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final ReceiveStorage receiveStorage = this.itemShopServer.getReceiveStorage();
        this.bukkitApiCore.getWindowManager().openWindow(player, new ReceiveGui(receiveStorage, receiveStorage.getReceiveContentFor(player.getUniqueId())));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitApiCore", this.bukkitApiCore).append("itemShopServer", this.itemShopServer).toString();
    }
}

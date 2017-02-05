package pl.north93.zgame.api.bukkitcommands;

import java.util.ResourceBundle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectResource;

public class HeadCmd extends NorthCommand
{
    @InjectResource(bundleName = "Commands")
    private ResourceBundle messages;

    public HeadCmd()
    {
        super("head");
        this.setPermission("api.command.head");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final PlayerInventory inv = player.getInventory();
        final ItemStack item = inv.getItemInMainHand();
        if (item == null || item.getType() == Material.AIR)
        {
            sender.sendMessage(this.messages, "command.head.empty_hand");
            return;
        }
        final ItemStack previousHelmet = inv.getHelmet();
        inv.setHelmet(item);
        inv.setItemInMainHand(previousHelmet);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

/**
 * Created by Konrad on 2017-02-24.
 */
public class AnvilCommand extends NorthCommand
{
    public AnvilCommand()
    {
        super("anvil", "kowadlo");
        this.setPermission("api.command.anvil");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final Inventory inventory = Bukkit.createInventory(player, InventoryType.ANVIL);

        player.openInventory(inventory);
    }
}

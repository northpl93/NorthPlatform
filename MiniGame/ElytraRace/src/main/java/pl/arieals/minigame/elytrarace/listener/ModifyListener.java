package pl.arieals.minigame.elytrarace.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ModifyListener implements Listener
{
    @EventHandler
    public void placeBlock(final BlockPlaceEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void destroyBlock(final BlockBreakEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryModify(final InventoryClickEvent event)
    {
        event.setCancelled(true);
    }
}

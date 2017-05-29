package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;

public class ModifyListener implements Listener
{
    @EventHandler
    public void placeBlock(final BlockPlaceEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData(event.getPlayer(), ElytraRacePlayer.class);
        if (playerData.isDev())
        {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void destroyBlock(final BlockBreakEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData(event.getPlayer(), ElytraRacePlayer.class);
        if (playerData.isDev())
        {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryModify(final InventoryClickEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData((Player) event.getWhoClicked(), ElytraRacePlayer.class);
        if (playerData.isDev())
        {
            return;
        }
        event.setCancelled(true);
    }
}

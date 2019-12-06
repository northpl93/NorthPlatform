package pl.arieals.minigame.elytrarace.listener;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class ModifyListener implements Listener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;

    @EventHandler
    public void placeBlock(final BlockPlaceEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData(event.getPlayer(), ElytraRacePlayer.class);
        if (playerData != null && playerData.isDev())
        {
            return;
        }
        this.messages.sendMessage(event.getPlayer(), "no_permissions");
        event.setCancelled(true);
    }

    @EventHandler
    public void destroyBlock(final BlockBreakEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData(event.getPlayer(), ElytraRacePlayer.class);
        if (playerData != null && playerData.isDev())
        {
            return;
        }
        this.messages.sendMessage(event.getPlayer(), "no_permissions");
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryModify(final InventoryClickEvent event)
    {
        final Player player = (Player) event.getWhoClicked();

        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData != null && playerData.isDev())
        {
            return;
        }

        this.messages.sendMessage(player, "no_permissions");
        event.setCancelled(true);
    }

    @EventHandler
    public void interact(final PlayerInteractEvent event)
    {
        final ElytraRacePlayer playerData = getPlayerData(event.getPlayer(), ElytraRacePlayer.class);
        if (playerData != null && playerData.isDev())
        {
            return;
        }
        this.messages.sendMessage(event.getPlayer(), "no_permissions");
        event.setCancelled(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

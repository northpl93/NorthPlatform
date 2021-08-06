package pl.north93.northplatform.minigame.elytrarace.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRacePlayer;

public class ModifyListener implements AutoListener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;

    @EventHandler
    public void placeBlock(final BlockPlaceEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
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
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
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
        final INorthPlayer player = INorthPlayer.wrap((Player) event.getWhoClicked());

        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
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
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
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

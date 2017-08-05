package pl.arieals.minigame.bedwars.shop.specialentry;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;

public class AdditionalLife implements IShopSpecialEntry, Listener
{
    private AdditionalLife(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBuy(final ItemBuyEvent event)
    {
        if (! "AdditionalLife".equals(event.getShopEntry().getSpecialHandler()))
        {
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        if (playerData == null)
        {
            event.setCancelled(true);
            return;
        }

        if (playerData.getLives() >= 1)
        {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean buy(final Player player, final Collection<ItemStack> items)
    {
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            return false;
        }

        playerData.addLive();
        return true;
    }
}
package pl.north93.northplatform.minigame.bedwars.shop.specialentry;

import java.util.Collection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.minigame.bedwars.event.ItemPreBuyEvent;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public class AdditionalLife implements IShopSpecialEntry, Listener
{
    private AdditionalLife(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuy(final ItemPreBuyEvent event)
    {
        if (! "AdditionalLife".equals(event.getShopEntry().getSpecialHandler()))
        {
            return;
        }

        final BedWarsPlayer playerData = event.getPlayer().getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        if (playerData.getLives() >= 1)
        {
            event.setBuyStatus(ItemPreBuyEvent.BuyStatus.ALREADY_HAVE);
        }
    }

    @Override
    public boolean buy(final INorthPlayer player, final Collection<ItemStack> items)
    {
        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return false;
        }

        playerData.addLive();
        return true;
    }
}

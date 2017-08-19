package pl.arieals.minigame.bedwars.shop.elimination;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class DiamondsEffect implements IEliminationEffect
{
    private static final int ITEMS_REMOVE = 20 * 5;

    @Override
    public String getName()
    {
        return "diamonds";
    }

    @Override
    public void playerEliminated(final Player player, final Player by)
    {
        final Location eyeLocation = player.getEyeLocation();

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            return;
        }

        final Collection<Item> items = IEliminationEffect.dropItems(eyeLocation, new ItemStack(Material.DIAMOND), 8);

        arena.getScheduler().runTaskLater(() ->
        {
            for (final Item item : items)
            {
                if (item.isDead())
                {
                    continue;
                }

                item.remove();
            }
        }, ITEMS_REMOVE);
    }
}

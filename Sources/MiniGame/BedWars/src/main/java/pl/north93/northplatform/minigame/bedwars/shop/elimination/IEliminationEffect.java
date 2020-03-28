package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public interface IEliminationEffect
{
    String getName();

    void playerEliminated(final LocalArena arena, INorthPlayer player, INorthPlayer by);

    /**
     * Util sluzacy do dropienia itemkow wokol danej lokacji.
     * @param location lokacja.
     * @param stack item do dropniecia.
     * @param amount ilosc.
     * @return Lista dropnietych przedmiotow.
     */
    static Collection<Item> dropItems(final Location location, final ItemStack stack, final int amount)
    {
        final Item[] items = new Item[amount];

        final World world = location.getWorld();
        for (int i = 0; i < amount; i++)
        {
            final CraftItem item = (CraftItem) world.dropItemNaturally(location, stack);
            item.setPickupDelay(Integer.MAX_VALUE);

            items[i] = item;
        }

        return Arrays.asList(items);
    }
}

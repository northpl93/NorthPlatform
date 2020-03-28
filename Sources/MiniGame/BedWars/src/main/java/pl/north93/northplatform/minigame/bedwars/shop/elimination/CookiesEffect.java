package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class CookiesEffect implements IEliminationEffect
{
    private static final int ITEMS_REMOVE = 20 * 5;

    @Override
    public String getName()
    {
        return "cookies";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location eyeLocation = player.getEyeLocation();

        final Collection<Item> items = IEliminationEffect.dropItems(eyeLocation, new ItemStack(Material.COOKIE), 8);

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

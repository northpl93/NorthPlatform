package pl.arieals.minigame.bedwars.shop.specialentry;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Specjalny sposób obsługi danego przedmiotu ze sklepu.
 * Np. wkładanie w odpowiednie miejsce itd.
 * <p>
 * Można także w configu nie ustawiać żadnych itemów,
 * a w tym handlerze zrobić coś innego. Wtedy {@code items}
 * będzie nullem.
 */
public interface IShopSpecialEntry
{
    boolean buy(Player player, Collection<ItemStack> items);
}

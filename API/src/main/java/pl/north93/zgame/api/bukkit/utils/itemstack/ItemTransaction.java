package pl.north93.zgame.api.bukkit.utils.itemstack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;

/**
 * Klasa pomocnicza ułatwiająca dodanie do ekwipunku listy itemów.
 * Najpierw zostanie podjęta próba dodania itemów, jak się nie uda
 * to nastąpi rollback.
 * <p>
 * Podsumowując doda albo wszystko z listy, albo nic i zwróci
 * boolean czy się udało.
 */
public final class ItemTransaction
{
    private ItemTransaction()
    {
    }

    public static boolean addItems(final Inventory inventory, final ItemStack[] itemsArray)
    {
        final HashMap<Integer, ItemStack> result = inventory.addItem(itemsArray);
        if (result.isEmpty())
        {
            return true;
        }

        final Set<Integer> failedItems = result.keySet();
        for (int i = 0; i < itemsArray.length; i++)
        {
            if (failedItems.contains(i))
            {
                continue;
            }

            inventory.removeItem(itemsArray[i]); // usuwamy item ktory udalo sie dodac
        }

        return false;
    }

    public static boolean addItems(final Inventory inventory, final Collection<ItemStack> items)
    {
        return addItems(inventory, items.toArray(new ItemStack[items.size()]));
    }

    public static boolean addXmlItems(final Inventory inventory, final Collection<XmlItemStack> items)
    {
        return addItems(inventory, items.stream().map(XmlItemStack::createItemStack).toArray(ItemStack[]::new));
    }
}

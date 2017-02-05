package pl.north93.zgame.skyblock.shop;

import static java.text.MessageFormat.format;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.windows.ClickInfo;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.api.ICategory;
import pl.north93.zgame.skyblock.shop.api.IShopEntry;
import pl.north93.zgame.skyblock.shop.server.gui.CategoryPicker;
import pl.north93.zgame.skyblock.shop.server.gui.CategoryView;

public class ShopManager
{
    private       BukkitApiCore                   apiCore;
    @InjectComponent("API.Economy")
    private       EconomyComponent                economyComponent;
    private final ICurrency                       currency;
    private final Multimap<ICategory, IShopEntry> shopEntries;

    public ShopManager(final ICurrency currency, final Multimap<ICategory, IShopEntry> shopEntries)
    {
        this.currency = currency;
        this.shopEntries = shopEntries;
    }

    public Set<ICategory> getCategories()
    {
        return this.shopEntries.keySet();
    }

    public ICategory getCategory(final String internalName)
    {
        return findInCollection(this.getCategories(), ICategory::getInternalName, internalName);
    }

    public void openCategoriesPicker(final Player player)
    {
        final CategoryPicker picker = new CategoryPicker(this.getCategories());
        this.apiCore.getWindowManager().openWindow(player, picker);
    }

    public void openCategory(final String category, final Player player)
    {
        this.openCategory(this.getCategory(category), player);
    }

    public void openCategory(final ICategory category, final Player player)
    {
        final Collection<IShopEntry> shopEntries = this.shopEntries.get(category);
        final CategoryView categoryView = new CategoryView(category.getDisplayName(), true, shopEntries);
        this.apiCore.getWindowManager().openWindow(player, categoryView);
    }

    public void processPayment(final IShopEntry shopEntry, final ClickInfo clickInfo)
    {
        final Player player = clickInfo.getWindow().getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final boolean isBuy = clickInfo.isLeftClick();
        final ItemStack itemStack = shopEntry.getBukkitItem().asBukkit();


        if (!isBuy && !inventory.containsAtLeast(itemStack, itemStack.getAmount()))
        {
            player.sendMessage(RED + "Nie masz wymaganej ilosci przedmiotow do sprzedania");
            return;
        }

        try (final ITransaction transaction = this.economyComponent.getEconomyManager().openTransaction(this.currency, player.getUniqueId()))
        {
            if (isBuy && shopEntry.canBuy())
            {
                final Double buyPrice = shopEntry.getBuyPrice();
                if (! transaction.has(buyPrice))
                {
                    player.sendMessage(RED + "Nie masz pieniedzy!");
                    return;
                }

                final HashMap<Integer, ItemStack> failed = inventory.addItem(itemStack);
                if (failed.isEmpty())
                {
                    transaction.remove(buyPrice);
                    player.sendMessage(GREEN + format("Kupiono {0} x {1} za {2}", itemStack.getAmount(), itemStack.getType(), buyPrice));
                }
                else
                {
                    player.sendMessage(RED + "Masz pelny ekwipunek");
                }
            }
            else if (!isBuy && shopEntry.canSell())
            {
                inventory.removeItem(itemStack);
                final Double sellPrice = shopEntry.getSellPrice();
                transaction.add(sellPrice);
                player.sendMessage(GREEN + format("Sprzedano {0} x {1} za {2}", itemStack.getAmount(), itemStack.getType(), sellPrice));
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public double getBalance(final Player player)
    {
        try (final ITransaction transaction = this.economyComponent.getEconomyManager().openTransaction(this.currency, player.getUniqueId()))
        {
            return transaction.getAmount();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
}

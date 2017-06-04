package pl.north93.zgame.skyblock.shop;

import static java.text.MessageFormat.format;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.windows.ClickInfo;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.skyblock.shop.api.ICategory;
import pl.north93.zgame.skyblock.shop.api.IShopEntry;
import pl.north93.zgame.skyblock.shop.gui.CategoryPicker;
import pl.north93.zgame.skyblock.shop.gui.CategoryView;

public class ShopManager
{
    @Inject
    private       BukkitApiCore                   apiCore;
    @Inject
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
        this.openCategory(category, player, this::openCategoriesPicker);
    }

    public void openCategory(final ICategory category, final Player player, final Consumer<Player> categoryPicker)
    {
        final Collection<IShopEntry> shopEntries = this.shopEntries.get(category);
        final CategoryView categoryView = new CategoryView(category, true, categoryPicker, shopEntries);
        this.apiCore.getWindowManager().openWindow(player, categoryView);
    }

    public void processPayment(final ICategory category, final IShopEntry shopEntry, final ClickInfo clickInfo)
    {
        final Player player = clickInfo.getWindow().getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final String permission = category.getPermission();
        if (! StringUtils.isEmpty(permission) && ! player.hasPermission(permission))
        {
            this.apiCore.getLogger().info("[SkyBlock.Shop] processPayment from=" + player.getName() + ", item=" + shopEntry + ", cancelled at permission check");
            player.sendMessage(RED + "Nie masz uprawnien do tej czesci sklepu");
            return;
        }

        final boolean isBuy = clickInfo.isLeftClick();
        final ItemStack itemStack = shopEntry.getBukkitItem().asBukkit();

        if (!isBuy && !inventory.containsAtLeast(itemStack, itemStack.getAmount()))
        {
            this.apiCore.getLogger().info("[SkyBlock.Shop] processPayment from=" + player.getName() + ", item=" + shopEntry + ", cancelled at !isBuy&&!inventory.containsAtLeast");
            player.sendMessage(RED + "Nie masz wymaganej ilosci przedmiotow do sprzedania");
            return;
        }

        try (final ITransaction transaction = this.economyComponent.getEconomyManager().openTransaction(this.currency, player.getUniqueId()))
        {
            final double amountBefore = transaction.getAmount();
            if (isBuy && shopEntry.canBuy())
            {
                final Double buyPrice = shopEntry.getBuyPrice();
                if (! transaction.has(buyPrice))
                {
                    this.apiCore.getLogger().info("[SkyBlock.Shop] processPayment from=" + player.getName() + ", item=" + shopEntry + ", amount=" + amountBefore + ", cancelled at !transaction.has");
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
            this.apiCore.getLogger().info("[SkyBlock.Shop] processPayment from=" + player.getName() + ", item=" + shopEntry + ", before=" + amountBefore + ", after=" + transaction.getAmount());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public double getBalance(final String player)
    {
        try (final ITransaction transaction = this.economyComponent.getEconomyManager().openTransaction(this.currency, player))
        {
            final double amount = transaction.getAmount();

            final String msg = "[SkyBlock.Shop] getBalance player={0},balance={1}";
            this.apiCore.getLogger().info(MessageFormat.format(msg, player, amount));

            return amount;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean pay(final UUID source, final String target, final double amount)
    {
        final IEconomyManager eco = this.economyComponent.getEconomyManager();
        try (final ITransaction t1 = eco.openTransaction(this.currency, source); final ITransaction t2 = eco.openTransaction(this.currency, target))
        {
            if (! t1.has(amount))
            {
                return false;
            }
            double fromBefore = t1.remove(amount);
            double targetBefore = t2.add(amount);

            final String msg = "[SkyBlock.Shop] pay from={0},fromBefore={1},to={2},toBefore={3},amount={4}";
            this.apiCore.getLogger().info(MessageFormat.format(msg, source, fromBefore, target, targetBefore, amount));
            return true;
        }
        catch (final PlayerNotFoundException e)
        {
            return false;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setMoney(final String target, final double amount)
    {
        final IEconomyManager eco = this.economyComponent.getEconomyManager();
        try (final ITransaction t = eco.openTransaction(this.currency, target))
        {
            final String msg = "[SkyBlock.Shop] setMoney target={0},amount={1},before={2}";
            this.apiCore.getLogger().info(MessageFormat.format(msg, target, amount, t.getAmount()));
            t.setAmount(amount);
            return true;
        }
        catch (final PlayerNotFoundException e)
        {
            return false;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
